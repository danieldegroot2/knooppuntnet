package kpn.core.engine.changes.integration

import kpn.core.analysis.Network
import kpn.core.test.TestData
import kpn.core.test.TestData2
import kpn.shared.ChangeSetElementRef
import kpn.shared.ChangeSetElementRefs
import kpn.shared.ChangeSetSubsetAnalysis
import kpn.shared.ChangeSetSummary
import kpn.shared.Country
import kpn.shared.NetworkChanges
import kpn.shared.NetworkType
import kpn.shared.Subset
import kpn.shared.changes.ChangeAction
import kpn.shared.changes.details.ChangeType
import kpn.shared.changes.details.NetworkChange
import kpn.shared.changes.details.NodeChange
import kpn.shared.changes.details.RouteChange
import kpn.shared.common.Ref
import kpn.shared.data.Tags
import kpn.shared.data.raw.RawMember
import kpn.shared.diff.RefDiffs
import kpn.shared.diff.TagDetail
import kpn.shared.diff.TagDetailType
import kpn.shared.diff.TagDiffs

class NetworkCreateTest06 extends AbstractTest {

  test("network create - with new route") {

    val dataBefore = TestData2()
      .networkNode(1001, "01")
      .networkNode(1002, "02")
      .data

    val dataAfter = TestData2()
      .networkNode(1001, "01")
      .networkNode(1002, "02", Tags.from("tag" -> "after"))
      .way(101, 1001, 1002)
      .route(11, "01-02", Seq(newMember("way", 101)))
      .networkRelation(1, "name", Seq(newMember("relation", 11)))
      .data

    val tc = new TestConfig()

    tc.relationAfter(dataAfter, 1) // new network relation
    tc.relationBefore(dataBefore, 11) // the route existed before
    tc.nodesBefore(dataBefore, 1001, 1002) // the network nodes existed before

    tc.process(ChangeAction.Create, TestData.relation(dataAfter, 1))

    tc.analysisData.networks.watched.contains(1) should equal(true)

    (tc.analysisRepository.saveNetwork _).verify(
      where { network: Network =>
        network.id should equal(1)
        // for remaining network structure - see NetworkAnalyzerTest
        true
      }
    ).once()

    (tc.changeSetRepository.saveChangeSetSummary _).verify(
      where { changeSetSummary: ChangeSetSummary =>
        changeSetSummary should equal(
          newChangeSetSummary(
            subsets = Seq(Subset.nlHiking),
            networkChanges = NetworkChanges(
              creates = Seq(
                newChangeSetNetwork(
                  Some(Country.nl),
                  NetworkType.hiking,
                  1,
                  "name",
                  routeChanges = ChangeSetElementRefs(
                    added = Seq(
                      ChangeSetElementRef(11, "01-02", happy = true, investigate = false)
                    )
                  ),
                  nodeChanges = ChangeSetElementRefs(
                    added = Seq(
                      ChangeSetElementRef(1001, "01", happy = true, investigate = false),
                      ChangeSetElementRef(1002, "02", happy = true, investigate = false)
                    )
                  ),
                  happy = true
                )
              )
            ),
            subsetAnalyses = Seq(
              ChangeSetSubsetAnalysis(Subset.nlHiking, happy = true)
            ),
            happy = true
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveNetworkChange _).verify(
      where { networkChange: NetworkChange =>
        networkChange should equal(
          newNetworkChange(
            newChangeKey(elementId = 1),
            ChangeType.Create,
            Some(Country.nl),
            NetworkType.hiking,
            1,
            "name",
            networkNodes = RefDiffs(added = Seq(Ref(1001, "01"), Ref(1002, "02"))),
            routes = RefDiffs(added = Seq(Ref(11, "01-02"))),
            happy = true
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveRouteChange _).verify(
      where { routeChange: RouteChange =>
        routeChange should equal(
          newRouteChange(
            newChangeKey(elementId = 11),
            ChangeType.Create,
            "01-02",
            addedToNetwork = Seq(Ref(1, "name")),
            before = None,
            after = Some(
              newRouteData(
                Some(Country.nl),
                NetworkType.hiking,
                newRawRelation(
                  11,
                  members = Seq(
                    RawMember("way", 101, None)
                  ),
                  tags = newRouteTags("01-02")
                ),
                "01-02",
                networkNodes = Seq(
                  newRawNodeWithName(1001, "01"),
                  newRawNodeWithName(1002, "02", Tags.from("tag" -> "after"))
                ),
                nodes = Seq(
                  newRawNodeWithName(1001, "01"),
                  newRawNodeWithName(1002, "02", Tags.from("tag" -> "after"))
                ),
                ways = Seq(
                  newRawWay(
                    101,
                    nodeIds = List(1001, 1002),
                    tags = Tags.from("highway" -> "unclassified")
                  )
                )
              )
            ),
            happy = true
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveNodeChange _).verify(
      where { nodeChange: NodeChange =>
        nodeChange.key.elementId match {
          case 1001 =>
            nodeChange should equal(
              newNodeChange(
                newChangeKey(elementId = 1001),
                ChangeType.Update,
                Seq(Subset.nlHiking),
                "01",
                before = Some(
                  newRawNodeWithName(1001, "01")
                ),
                after = Some(
                  newRawNodeWithName(1001, "01")
                ),
                addedToRoute = Seq(
                  Ref(11, "01-02")
                ),
                addedToNetwork = Seq(
                  Ref(1, "name")
                ),
                happy = true
              )
            )
            true

          case 1002 =>

            nodeChange should equal(
              newNodeChange(
                newChangeKey(elementId = 1002),
                ChangeType.Update,
                Seq(Subset.nlHiking),
                "02",
                before = Some(
                  newRawNodeWithName(1002, "02")
                ),
                after = Some(
                  newRawNodeWithName(1002, "02", Tags.from("tag" -> "after"))
                ),
                tagDiffs = Some(
                  TagDiffs(
                    mainTags = Seq(
                      TagDetail(TagDetailType.Same,"rwn_ref",Some("02"),Some("02"))
                    ),
                    extraTags = Seq(
                      TagDetail(TagDetailType.Add,"tag",None,Some("after"))
                    )
                  )
                ),
                addedToRoute = Seq(
                  Ref(11, "01-02")
                ),
                addedToNetwork = Seq(
                  Ref(1, "name")
                ),
                happy = true
              )
            )
            true

          case _ => false
        }
      }
    ).repeated(2)
  }
}
