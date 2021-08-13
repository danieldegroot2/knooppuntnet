package kpn.server.analyzer.engine.changes.integration

import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.NetworkChanges
import kpn.api.common.changes.ChangeAction
import kpn.api.common.changes.details.ChangeType
import kpn.api.common.common.Ref
import kpn.api.custom.Country
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.core.test.OverpassData
import kpn.core.test.TestSupport.withDatabase

class NetworkDeleteNodeTest02 extends AbstractIntegrationTest {

  test("network delete - node still referenced in other network does not become orphan") {

    val dataBefore = OverpassData()
      .networkNode(1001, "01")
      .networkRelation(1, "network1", Seq(newMember("node", 1001)))
      .networkRelation(2, "network2", Seq(newMember("node", 1001)))

    val dataAfter = OverpassData()
      .networkNode(1001, "01")
      .networkRelation(2, "network2", Seq(newMember("node", 1001)))

    withDatabase { database =>

      val tc = new IntegrationTestContext(database, dataBefore, dataAfter)

      tc.process(ChangeAction.Delete, newRawRelation(1))

      assert(!tc.analysisContext.data.networks.watched.contains(1))
      assert(tc.analysisContext.data.networks.watched.contains(2))
      assert(!tc.analysisContext.data.nodes.watched.contains(1001))

      tc.findNetworkInfoById(1) should matchTo(
        newNetworkInfoDoc(
          1,
          active = false, // <--- !!!
          country = Some(Country.nl),
          newNetworkSummary(
            name = "network1",
            networkType = NetworkType.hiking,
          ),
          newNetworkDetail(
            lastUpdated = timestampAfterValue,
            relationLastUpdated = timestampAfterValue
          )
        )
      )

      tc.findChangeSetSummaryById("123:1") should matchTo(
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

      tc.findNetworkInfoChangeById("123:1:1") should matchTo(
        newNetworkInfoChange(
          newChangeKey(elementId = 1),
          ChangeType.Delete,
          Some(Country.nl),
          NetworkType.hiking,
          1,
          "network1",
          investigate = true
        )
      )

      assert(database.routeChanges.findAll().isEmpty)

      tc.findNodeChangeById("123:1:1001") should matchTo(
        newNodeChange(
          key = newChangeKey(elementId = 1001),
          changeType = ChangeType.Update,
          subsets = Seq(Subset.nlHiking),
          name = "01",
          before = Some(
            newMetaData()
          ),
          after = Some(
            newMetaData()
          ),
          removedFromNetwork = Seq(
            Ref(1, "network1")
          ),
          investigate = true,
          impact = true
        )
      )
    }
  }
}
