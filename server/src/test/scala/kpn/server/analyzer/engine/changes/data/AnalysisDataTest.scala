package kpn.server.analyzer.engine.changes.data

import kpn.api.common.SharedTestObjects
import kpn.core.data.Data
import kpn.core.test.TestData2
import kpn.core.util.UnitTest
import kpn.server.analyzer.engine.changes.changes.RelationAnalyzer

class AnalysisDataTest extends UnitTest with SharedTestObjects {

  test("isReferencedRoute") {

    val data = buildData()

    val analysisData = AnalysisData()
    analysisData.networks.watched.add(1, RelationAnalyzer.toElementIds(data.relations(1)))
    analysisData.networks.watched.add(2, RelationAnalyzer.toElementIds(data.relations(2)))

    assert(analysisData.networks.isReferencingRelation(11))
    assert(analysisData.networks.isReferencingRelation(12))
    assert(!analysisData.networks.isReferencingRelation(13))
  }

  private def buildData(): Data = {
    TestData2()
      .networkNode(1001, "01") // referenced in network1 and network2 and orphan route
      .networkNode(1002, "02") // referenced in network1
      .networkNode(1003, "03") // referenced in network2
      .networkNode(1004, "04") // referenced in orphan route
      .way(101, 1001, 1002) // network 1
      .route(11, "01-02", Seq(newMember("way", 101)))
      .networkRelation(1, "network1", Seq(newMember("relation", 11)))
      .way(102, 1001, 1003) // network 2
      .route(12, "01-03", Seq(newMember("way", 102)))
      .networkRelation(2, "network2", Seq(newMember("relation", 12)))
      .way(103, 1001, 1003) // orphan route
      .route(13, "01-04", Seq(newMember("way", 103)))
      .data
  }
}
