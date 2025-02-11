package kpn.server.analyzer.engine.changes.integration

import kpn.api.common.ChangeSetElementRefs
import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.NetworkChanges
import kpn.api.common.changes.ChangeAction
import kpn.api.common.common.Ref
import kpn.api.common.diff.RefDiffs
import kpn.api.custom.ChangeType
import kpn.api.custom.Country
import kpn.api.custom.Fact
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.api.custom.Tags
import kpn.core.doc.Label
import kpn.core.test.OverpassData

class NetworkDeleteNodeTest04 extends IntegrationTest {

  test("network delete - node looses node tag") {

    val dataBefore = OverpassData()
      .networkNode(1001, "01", version = 1)
      .networkRelation(
        1,
        "network",
        Seq(
          newMember("node", 1001)
        )
      )

    val dataAfter = OverpassData()
      .node(1001, version = 2)

    testIntegration(dataBefore, dataAfter) {

      process(ChangeAction.Delete, newRawRelation(1))

      assert(database.orphanNodes.isEmpty) // the node does not become orphan, it is no longer a network node

      assert(!watched.networks.contains(1))
      assert(!watched.nodes.contains(1001))

      assertNetworkInfo()
      assertNode()
      assertNetworkInfoChange()
      assertNodeChange()
      assertChangeSetSummary()
    }
  }

  private def assertNetworkInfo(): Unit = {
    findNetworkInfoById(1).shouldMatchTo(
      newNetworkInfoDoc(
        1,
        active = false, // <--- !!!
        country = Some(Country.nl),
        newNetworkSummary(
          name = "network",
          networkType = NetworkType.hiking,
          changeCount = 1
        ),
        newNetworkDetail(
          lastUpdated = defaultTimestamp,
          relationLastUpdated = defaultTimestamp,
          tags = newNetworkTags("network")
        )
      )
    )
  }

  private def assertNode(): Unit = {
    findNodeById(1001).shouldMatchTo(
      newNodeDoc(
        1001,
        labels = Seq(
          Label.networkType(NetworkType.hiking)
          // not active
        ),
        country = Some(Country.nl),
        name = "01",
        names = Seq(newNodeName(name = "01")),
        version = 1, // <--
        tags = Tags.from(
          "rwn_ref" -> "01",
          "network:type" -> "node_network",
        )
      )
    )
  }

  private def assertNetworkInfoChange(): Unit = {
    findNetworkInfoChangeById("123:1:1").shouldMatchTo(
      newNetworkInfoChange(
        newChangeKey(elementId = 1),
        ChangeType.Delete,
        Some(Country.nl),
        NetworkType.hiking,
        1,
        "network",
        nodeDiffs = RefDiffs(
          removed = Seq(
            Ref(1001, "01")
          )
        ),
        investigate = true
      )
    )
  }

  private def assertNodeChange(): Unit = {
    findNodeChangeById("123:1:1001").shouldMatchTo(
      newNodeChange(
        key = newChangeKey(elementId = 1001),
        changeType = ChangeType.Delete,
        subsets = Seq(Subset.nlHiking),
        name = "01",
        before = Some(
          newMetaData(version = 1)
        ),
        after = None,
        tagDiffs = None,
        removedFromNetwork = Seq(
          Ref(1, "network")
        ),
        facts = Seq(Fact.Deleted),
        investigate = true,
        impact = true,
        locationInvestigate = true,
        locationImpact = true
      )
    )
  }

  private def assertChangeSetSummary(): Unit = {
    findChangeSetSummaryById("123:1").shouldMatchTo(
      newChangeSetSummary(
        subsets = Seq(Subset.nlHiking),
        networkChanges = NetworkChanges(
          deletes = Seq(
            newChangeSetNetwork(
              Some(Country.nl),
              NetworkType.hiking,
              1,
              "network",
              nodeChanges = ChangeSetElementRefs(
                removed = Seq(
                  newChangeSetElementRef(1001, "01", investigate = true)
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
  }
}
