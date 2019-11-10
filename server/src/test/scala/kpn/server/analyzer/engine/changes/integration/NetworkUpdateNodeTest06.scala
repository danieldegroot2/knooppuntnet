package kpn.server.analyzer.engine.changes.integration

import kpn.api.custom.Country
import kpn.api.custom.Fact
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.api.custom.Tags
import kpn.core.test.TestData2
import kpn.api.common.ChangeSetElementRefs
import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.ChangeSetSummary
import kpn.api.common.NetworkChanges
import kpn.api.common.NodeInfo
import kpn.api.common.changes.ChangeAction
import kpn.api.common.changes.details.ChangeType
import kpn.api.common.changes.details.NetworkChange
import kpn.api.common.changes.details.NodeChange
import kpn.api.common.common.Ref
import kpn.api.common.data.raw.RawMember
import kpn.api.common.diff.NetworkData
import kpn.api.common.diff.NetworkDataUpdate
import kpn.api.common.diff.RefDiffs
import kpn.api.common.diff.TagDetail
import kpn.api.common.diff.TagDetailType
import kpn.api.common.diff.TagDiffs

class NetworkUpdateNodeTest06 extends AbstractTest {

  test("network update - removed node that looses required tags, but still has tags of other networkType does not become inactive") {

    val dataBefore = TestData2()
      .networkNode(1001, "01")
      .node(1002, tags = Tags.from("rwn_ref" -> "02", "rcn_ref" -> "03", "network:type" -> "node_network"))
      .networkRelation(
        1,
        "name",
        Seq(
          newMember("node", 1001),
          newMember("node", 1002)
        )
      )
      .data

    val dataAfter = TestData2()
      .networkNode(1001, "01")
      .node(1002, tags = Tags.from("rcn_ref" -> "03", "network:type" -> "node_network"))
      .networkRelation(
        1,
        "name",
        Seq(
          newMember("node", 1001)
          // node 02 no longer part of the network
        )
      )
      .data

    val tc = new TestConfig()
    tc.relationBefore(dataBefore, 1)
    tc.watchNetwork(dataBefore, 1)
    tc.relationAfter(dataAfter, 1)
    tc.nodeAfter(dataAfter, 1002)
    tc.nodesAfter(dataAfter, 1002)

    // before:
    tc.analysisContext.data.networks.watched.isReferencingNode(1002) should equal(true)
    tc.analysisContext.data.orphanNodes.watched.contains(1001) should equal(false)

    // act:
    tc.process(ChangeAction.Modify, relation(dataAfter, 1))

    // after:
    tc.analysisContext.data.networks.watched.isReferencingNode(1002) should equal(false)
    tc.analysisContext.data.orphanNodes.watched.contains(1001) should equal(false)

    (tc.analysisRepository.saveNetwork _).verify(*).once()

    (tc.analysisRepository.saveRoute _).verify(*).never()

    (tc.analysisRepository.saveNode _).verify(
      where { nodeInfo: NodeInfo =>
        nodeInfo should equal(
          newNodeInfo(
            1002,
            country = Some(Country.nl),
            tags = Tags.from("rcn_ref" -> "03", "network:type" -> "node_network")
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveChangeSetSummary _).verify(
      where { changeSetSummary: ChangeSetSummary =>
        changeSetSummary should equal(
          newChangeSetSummary(
            subsets = Seq(Subset.nlHiking),
            networkChanges = NetworkChanges(
              updates = Seq(
                newChangeSetNetwork(
                  Some(Country.nl),
                  NetworkType.hiking,
                  1,
                  "name",
                  nodeChanges = ChangeSetElementRefs(
                    removed = Seq(
                      newChangeSetElementRef(1002, "02", investigate = true)
                    )
                  ),
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
        networkChange should equal(
          newNetworkChange(
            newChangeKey(elementId = 1),
            ChangeType.Update,
            Some(Country.nl),
            NetworkType.hiking,
            1,
            "name",
            networkDataUpdate = Some(
              NetworkDataUpdate(
                NetworkData(
                  newRawRelation(
                    1,
                    members = List(
                      RawMember("node", 1001, None),
                      RawMember("node", 1002, None)
                    ),
                    tags = newNetworkTags()
                  ),
                  "name"
                ),
                NetworkData(
                  newRawRelation(
                    1,
                    members = Seq(RawMember("node", 1001, None)),
                    tags = newNetworkTags()
                  ),
                  "name"
                )
              )
            ),
            networkNodes = RefDiffs(
              removed = Seq(Ref(1002, "02"))
            ),
            investigate = true
          )
        )
        true
      }
    )

    (tc.changeSetRepository.saveNodeChange _).verify(
      where { nodeChange: NodeChange =>
        nodeChange should equal(
          newNodeChange(
            newChangeKey(elementId = 1002),
            ChangeType.Update,
            Seq(Subset.nlHiking),
            "03",
            before = Some(
              newRawNode(1002, tags = Tags.from("rwn_ref" -> "02", "rcn_ref" -> "03", "network:type" -> "node_network"))
            ),
            after = Some(
              newRawNode(1002, tags = Tags.from("rcn_ref" -> "03", "network:type" -> "node_network"))
            ),
            tagDiffs = Some(
              TagDiffs(
                Seq(
                  TagDetail(TagDetailType.Same, "rcn_ref", Some("03"), Some("03")),
                  TagDetail(TagDetailType.Delete, "rwn_ref", Some("02"), None),
                  TagDetail(TagDetailType.Same, "network:type", Some("node_network"), Some("node_network"))
                ),
                Seq()
              )
            ),
            removedFromNetwork = Seq(Ref(1, "name")),
            facts = Seq(Fact.LostHikingNodeTag),
            investigate = true
          )
        )
        true
      }
    )
  }
}
