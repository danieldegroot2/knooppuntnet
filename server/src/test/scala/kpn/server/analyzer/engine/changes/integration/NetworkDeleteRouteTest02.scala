package kpn.server.analyzer.engine.changes.integration

import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.ChangeSetSummary
import kpn.api.common.NetworkChanges
import kpn.api.common.changes.ChangeAction
import kpn.api.common.changes.details.ChangeType
import kpn.api.common.changes.details.NetworkChange
import kpn.api.common.changes.details.NodeChange
import kpn.api.common.changes.details.RouteChange
import kpn.api.common.common.Ref
import kpn.api.common.data.raw.RawMember
import kpn.api.common.network.NetworkInfo
import kpn.api.custom.Country
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.api.custom.Tags
import kpn.core.test.OverpassData
import kpn.server.analyzer.engine.changes.changes.RelationAnalyzer

class NetworkDeleteRouteTest02 extends AbstractTest {

  test("network delete - route still referenced in other network does not become orphan") {

    pending

    val dataBefore = OverpassData()
      .networkNode(1001, "01")
      .networkNode(1002, "02")
      .way(101, 1001, 1002)
      .route(11, "01-02", Seq(newMember("way", 101)))
      .networkRelation(1, "network1", Seq(newMember("relation", 11)))
      .networkRelation(2, "network2", Seq(newMember("relation", 11)))
      .data

    val dataAfter = OverpassData()
      .networkNode(1001, "01")
      .networkNode(1002, "02")
      .way(101, 1001, 1002)
      .route(11, "01-02", Seq(newMember("way", 101)))
      .networkRelation(2, "network2", Seq(newMember("relation", 12)))
      .data

    val tc = new OldTestConfig(dataBefore, dataAfter)
    tc.relationBefore(dataBefore, 1)
    tc.relationAfter(dataAfter, 11)
    tc.nodesAfter(dataAfter, 1001, 1002)

    tc.analysisContext.data.networks.watched.add(1, RelationAnalyzer.toElementIds(dataBefore.relations(1)))
    tc.analysisContext.data.networks.watched.add(2, RelationAnalyzer.toElementIds(dataBefore.relations(2)))

    tc.process(ChangeAction.Delete, newRawRelation(1))

    assert(!tc.analysisContext.data.networks.watched.contains(1))
    assert(tc.analysisContext.data.networks.watched.contains(2))
    assert(!tc.analysisContext.data.routes.watched.contains(11))

    assert(!tc.analysisContext.data.orphanNodes.watched.contains(1001))
    assert(!tc.analysisContext.data.orphanNodes.watched.contains(1002))

    (tc.networkRepository.oldSaveNetworkInfo _).verify(
      where { networkInfo: NetworkInfo =>
        networkInfo should matchTo(
          newNetworkInfo(
            newNetworkAttributes(
              1,
              Some(Country.nl),
              NetworkType.hiking,
              name = "network1",
              lastUpdated = timestampAfterValue,
              relationLastUpdated = timestampAfterValue
            ),
            active = false // <--- !!!
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveChangeSetSummary _).verify(
      where { changeSetSummary: ChangeSetSummary =>
        changeSetSummary should matchTo(
          newChangeSetSummary(
            subsets = Seq(Subset.nlHiking),
            networkChanges = NetworkChanges(
              deletes = Seq(
                newChangeSetNetwork(
                  Some(Country.nl),
                  NetworkType.hiking,
                  1,
                  "network1",
                  investigate = true
                )
              )
            ),
            subsetAnalyses = Seq(
              ChangeSetSubsetAnalysis(Subset.nlHiking, investigate = true)
            ),
            investigate = true
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveNetworkChange _).verify(
      where { networkChange: NetworkChange =>
        networkChange should matchTo(
          newNetworkChange(
            newChangeKey(elementId = 1),
            ChangeType.Delete,
            Some(Country.nl),
            NetworkType.hiking,
            1,
            "network1",
            investigate = true
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveRouteChange _).verify(
      where { routeChange: RouteChange =>

        val routeData = newRouteData(
          Some(Country.nl),
          NetworkType.hiking,
          relation = newRawRelation(
            11,
            members = Seq(
              RawMember("way", 101, None)
            ),
            tags = newRouteTags("01-02")
          ),
          name = "01-02",
          networkNodes = Seq(
            newRawNodeWithName(1001, "01"),
            newRawNodeWithName(1002, "02")
          ),
          nodes = Seq(
            newRawNodeWithName(1001, "01"),
            newRawNodeWithName(1002, "02")
          ),
          ways = Seq(
            newRawWay(
              101,
              nodeIds = Seq(1001, 1002),
              tags = Tags.from("highway" -> "unclassified")
            )
          )
        )

        routeChange should matchTo(
          newRouteChange(
            newChangeKey(elementId = 11),
            ChangeType.Update,
            "01-02",
            removedFromNetwork = Seq(Ref(1, "network1")),
            before = Some(routeData),
            after = Some(routeData),
            investigate = true,
            impact = true
          )
        )

        true
      }
    )

    (tc.changeSetRepository.saveNodeChange _).verify(
      where { nodeChange: NodeChange =>

        if (nodeChange.id == 1001) {
          nodeChange should matchTo(
            newNodeChange(
              key = newChangeKey(elementId = 1001),
              changeType = ChangeType.Update,
              subsets = Seq(Subset.nlHiking),
              name = "01",
              before = Some(
                newRawNodeWithName(1001, "01")
              ),
              after = Some(
                newRawNodeWithName(1001, "01")
              ),
              removedFromNetwork = Seq(
                Ref(1, "network1")
              ),
              investigate = true,
              impact = true
            )
          )
        }
        else if (nodeChange.id == 1002) {
          nodeChange should matchTo(
            newNodeChange(
              key = newChangeKey(elementId = 1002),
              changeType = ChangeType.Update,
              subsets = Seq(Subset.nlHiking),
              name = "02",
              before = Some(
                newRawNodeWithName(1002, "02")
              ),
              after = Some(
                newRawNodeWithName(1002, "02")
              ),
              removedFromNetwork = Seq(
                Ref(1, "network1")
              ),
              investigate = true,
              impact = true
            )
          )
        }
        else {
          fail(s"Unexpected node id ${nodeChange.id}")
        }

        true
      }
    ).repeated(2)
  }
}
