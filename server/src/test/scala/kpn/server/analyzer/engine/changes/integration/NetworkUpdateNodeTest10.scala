package kpn.server.analyzer.engine.changes.integration

import kpn.api.common.ChangeSetElementRefs
import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.NetworkChanges
import kpn.api.common.changes.ChangeAction
import kpn.api.common.changes.details.RefBooleanChange
import kpn.api.common.common.Ref
import kpn.api.common.diff.RefDiffs
import kpn.api.custom.ChangeType
import kpn.api.custom.Country
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.core.test.OverpassData

class NetworkUpdateNodeTest10 extends IntegrationTest {

  test("network update - node role 'connection' added") {

    val dataBefore = OverpassData()
      .networkNode(1001, "01")
      .networkRelation(
        1,
        "network-name",
        Seq(
          newMember("node", 1001)
        )
      )

    val dataAfter = OverpassData()
      .networkNode(1001, "01")
      .networkRelation(
        1,
        "network-name",
        Seq(
          newMember("node", 1001, "connection")
        )
      )

    testIntegration(dataBefore, dataAfter) {

      process(ChangeAction.Modify, dataAfter.rawRelationWithId(1))

      assertNetwork()
      assertNetworkInfo()
      assertNetworkInfoChange()
      assertNodeChange()
      assertChangeSetSummary()
    }
  }

  private def assertNetwork(): Unit = {
    val networkDoc = findNetworkById(1)
    networkDoc._id should equal(1)
  }

  private def assertNetworkInfo(): Unit = {
    val networkInfoDoc = findNetworkInfoById(1)
    networkInfoDoc._id should equal(1)
  }

  private def assertNetworkInfoChange(): Unit = {
    findNetworkInfoChangeById("123:1:1").shouldMatchTo(
      newNetworkInfoChange(
        newChangeKey(elementId = 1),
        ChangeType.Update,
        Some(Country.nl),
        NetworkType.hiking,
        1,
        "network-name",
        networkDataUpdate = None,
        nodeDiffs = RefDiffs(
          updated = Seq(
            Ref(1001, "01")
          )
        )
      )
    )
  }

  private def assertNodeChange(): Unit = {
    findNodeChangeById("123:1:1001").shouldMatchTo(
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
        roleConnectionChanges = Seq(
          RefBooleanChange(Ref(1, "network-name"), after = true)
        )
      )
    )
  }

  private def assertChangeSetSummary(): Unit = {
    findChangeSetSummaryById("123:1").shouldMatchTo(
      newChangeSetSummary(
        subsets = Seq(Subset.nlHiking),
        networkChanges = NetworkChanges(
          updates = Seq(
            newChangeSetNetwork(
              Some(Country.nl),
              NetworkType.hiking,
              1,
              "network-name",
              nodeChanges = ChangeSetElementRefs(
                updated = Seq(newChangeSetElementRef(1001, "01"))
              )
            )
          )
        ),
        subsetAnalyses = Seq(
          ChangeSetSubsetAnalysis(Subset.nlHiking)
        )
      )
    )
  }
}
