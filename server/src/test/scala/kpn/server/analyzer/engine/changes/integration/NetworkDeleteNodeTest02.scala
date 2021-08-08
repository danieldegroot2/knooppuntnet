package kpn.server.analyzer.engine.changes.integration

import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.ChangeSetSummary
import kpn.api.common.NetworkChanges
import kpn.api.common.changes.ChangeAction
import kpn.api.common.changes.details.ChangeType
import kpn.api.common.changes.details.NetworkChange
import kpn.api.common.changes.details.NodeChange
import kpn.api.common.common.Ref
import kpn.api.common.network.NetworkInfo
import kpn.api.custom.Country
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.core.test.TestData2
import kpn.server.analyzer.engine.changes.changes.RelationAnalyzer

class NetworkDeleteNodeTest02 extends AbstractTest {

  test("network delete - node still referenced in other network does not become orphan") {

    pending

    val dataBefore = TestData2()
      .networkNode(1001, "01")
      .networkRelation(1, "network1", Seq(newMember("node", 1001)))
      .networkRelation(2, "network2", Seq(newMember("node", 1001)))
      .data

    val dataAfter = TestData2()
      .networkNode(1001, "01")
      .networkRelation(2, "network2", Seq(newMember("node", 1001)))
      .data

    val tc = new TestConfig()
    tc.relationBefore(dataBefore, 1)
    tc.nodesAfter(dataAfter, 1001)

    tc.analysisContext.data.networks.watched.add(1, RelationAnalyzer.toElementIds(dataBefore.relations(1)))
    tc.analysisContext.data.networks.watched.add(2, RelationAnalyzer.toElementIds(dataBefore.relations(2)))

    tc.process(ChangeAction.Delete, newRawRelation(1))

    assert(!tc.analysisContext.data.networks.watched.contains(1))
    assert(tc.analysisContext.data.networks.watched.contains(2))
    assert(!tc.analysisContext.data.orphanNodes.watched.contains(1001))

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

    (tc.changeSetRepository.saveRouteChange _).verify(*).never()

    (tc.changeSetRepository.saveNodeChange _).verify(
      where { nodeChange: NodeChange =>
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
        true
      }
    )
  }
}
