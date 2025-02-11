package kpn.server.analyzer.engine.changes.integration

import kpn.api.common.ChangeSetElementRefs
import kpn.api.common.ChangeSetSubsetAnalysis
import kpn.api.common.ChangeSetSubsetElementRefs
import kpn.api.common.NetworkChanges
import kpn.api.common.changes.ChangeAction
import kpn.api.common.common.Ref
import kpn.api.common.data.raw.RawMember
import kpn.api.common.diff.RefDiffs
import kpn.api.custom.ChangeType
import kpn.api.custom.Country
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.api.custom.Tags
import kpn.core.test.OverpassData

class NetworkDeleteRouteTest03 extends IntegrationTest {

  test("network delete - route looses route tags") {

    val dataBefore = OverpassData()
      .networkNode(1001, "01") // referenced in network1 and network2 and orphan route
      .networkNode(1002, "02") // referenced in network1
      .networkNode(1003, "03") // referenced in network2
      .networkNode(1004, "04") // referenced in orphan route
      .way(101, 1001, 1002) // route 11 only referenced in network 1
      .route(11, "01-02", Seq(newMember("way", 101)))
      .way(102, 1001, 1003) // route 12 referenced in network 1 and network 2
      .route(12, "01-03", Seq(newMember("way", 102)))
      .networkRelation(1, "network1", Seq(newMember("relation", 11), newMember("relation", 12)))
      .networkRelation(2, "network2", Seq(newMember("relation", 12)))

    val dataAfter = OverpassData()
      .networkNode(1001, "01")
      .networkNode(1002, "02")
      .networkNode(1003, "03")
      .networkNode(1004, "04")
      .way(101, 1001, 1002)
      .route(11, "01-02", Seq(newMember("way", 101))) // route has become orphan
      .way(102, 1001, 1003) // route 12 still referenced in network 2
      .route(12, "01-03", Seq(newMember("way", 102)))
      .networkRelation(2, "network2", Seq(newMember("relation", 12)))

    testIntegration(dataBefore, dataAfter) {

      process(ChangeAction.Delete, newRawRelation(1))

      // network 1 is no longer in memory
      assert(!watched.networks.contains(1))

      assert(watched.routes.contains(11)) // network 1 was removed, route no longer referenced
      assert(watched.routes.contains(12)) // network 1 was removed, but route still referenced in network 2

      assert(watched.nodes.contains(1001))
      assert(watched.nodes.contains(1002)) // still referenced in orphan route
      assert(watched.nodes.contains(1003))

      assertNetworkInfo()
      assertNetworkInfoChange()
      assertRoute11()
      assertRoute12()
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
          name = "network1",
          networkType = NetworkType.hiking,
          changeCount = 1
        ),
        newNetworkDetail(
          lastUpdated = defaultTimestamp,
          relationLastUpdated = defaultTimestamp,
          tags = newNetworkTags("network1")
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
        "network1",
        nodeDiffs = RefDiffs(
          removed = Seq(
            Ref(1001, "01"),
            Ref(1002, "02"),
            Ref(1003, "03")
          )
        ),
        routeDiffs = RefDiffs(
          removed = Seq(
            Ref(11, "01-02"),
            Ref(12, "01-03")
          )
        ),
        investigate = true
      )
    )
  }

  private def assertRoute11(): Unit = {

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
          nodeIds = Vector(1001, 1002),
          tags = Tags.from("highway" -> "unclassified")
        )
      ),
    )

    findRouteChangeById("123:1:11").shouldMatchTo(
      newRouteChange(
        newChangeKey(elementId = 11),
        ChangeType.Update,
        "01-02",
        removedFromNetwork = Seq(Ref(1, "network1")),
        before = Some(routeData),
        after = Some(routeData),
        impactedNodeIds = Seq(1001, 1002),
        investigate = true,
        impact = true
      )
    )
  }

  private def assertRoute12(): Unit = {

    val routeData = newRouteData(
      Some(Country.nl),
      NetworkType.hiking,
      relation = newRawRelation(
        12,
        members = Seq(
          RawMember("way", 102, None)
        ),
        tags = newRouteTags("01-03")
      ),
      name = "01-03",
      networkNodes = Seq(
        newRawNodeWithName(1001, "01"),
        newRawNodeWithName(1003, "03")
      ),
      nodes = Seq(
        newRawNodeWithName(1001, "01"),
        newRawNodeWithName(1003, "03")
      ),
      ways = Seq(
        newRawWay(
          102,
          nodeIds = Vector(1001, 1003),
          tags = Tags.from("highway" -> "unclassified")
        )
      )
    )

    findRouteChangeById("123:1:12").shouldMatchTo(
      newRouteChange(
        newChangeKey(elementId = 12),
        ChangeType.Update,
        "01-03",
        removedFromNetwork = Seq(Ref(1, "network1")),
        before = Some(routeData),
        after = Some(routeData),
        impactedNodeIds = Seq(1001, 1003),
        investigate = true,
        impact = true
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
              "network1",
              routeChanges = ChangeSetElementRefs(
                removed = Seq(
                  newChangeSetElementRef(11, "01-02", investigate = true),
                  newChangeSetElementRef(12, "01-03", investigate = true)
                )
              ),
              nodeChanges = ChangeSetElementRefs(
                removed = Seq(
                  newChangeSetElementRef(1001, "01", investigate = true),
                  newChangeSetElementRef(1002, "02", investigate = true),
                  newChangeSetElementRef(1003, "03", investigate = true)
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
