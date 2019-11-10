package kpn.server.analyzer.engine.changes.integration

import kpn.api.custom.Country
import kpn.api.custom.Fact
import kpn.api.custom.NetworkScope
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.api.custom.Timestamp
import kpn.core.test.TestData
import kpn.core.test.TestData2
import kpn.api.common.ChangeSetElementRefs
import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.ChangeSetSubsetElementRefs
import kpn.api.common.ChangeSetSummary
import kpn.api.common.NodeInfo
import kpn.api.common.NodeName
import kpn.api.common.ScopedNetworkType
import kpn.api.common.changes.ChangeAction
import kpn.api.common.changes.details.ChangeType
import kpn.api.common.changes.details.NodeChange

class OrphanNodeTest03 extends AbstractTest {

  test("delete orphan node") {

    val dataBefore = TestData2()
      .networkNode(1001, "01")
      .data

    val dataAfter = new TestData().data

    val tc = new TestConfig()

    tc.analysisContext.data.orphanNodes.watched.add(1001)

    tc.nodesBefore(dataBefore, 1001)
    tc.nodeAfter(dataAfter, 1001)

    tc.process(ChangeAction.Delete, newRawNode(1001))

    tc.analysisContext.data.orphanNodes.watched.contains(1001) should equal(false)

    (tc.analysisRepository.saveNode _).verify(
      where { nodeInfo: NodeInfo =>
        nodeInfo should equal(
          NodeInfo(
            1001,
            active = false, // <-- !!
            orphan = true,
            Some(Country.nl),
            "01",
            Seq(NodeName(ScopedNetworkType(NetworkScope.regional, NetworkType.hiking), "01")),
            "0",
            "0",
            Timestamp(2015, 8, 11, 0, 0, 0),
            newNodeTags("01"),
            Seq(Fact.Deleted),
            None
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
            orphanNodeChanges = Seq(
              ChangeSetSubsetElementRefs(
                Subset.nlHiking,
                ChangeSetElementRefs(
                  removed = Seq(
                    newChangeSetElementRef(1001, "01", investigate = true)
                  )
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

    (tc.changeSetRepository.saveNodeChange _).verify(
      where { nodeChange: NodeChange =>
        nodeChange should equal(
          newNodeChange(
            newChangeKey(elementId = 1001),
            ChangeType.Delete,
            Seq(Subset.nlHiking),
            "01",
            before = Some(
              newRawNodeWithName(1001, "01")
            ),
            facts = Seq(Fact.WasOrphan, Fact.Deleted),
            investigate = true
          )
        )
        true
      }
    )
  }
}
