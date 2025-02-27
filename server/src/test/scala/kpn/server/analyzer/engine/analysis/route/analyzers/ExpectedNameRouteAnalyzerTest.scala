package kpn.server.analyzer.engine.analysis.route.analyzers

import kpn.api.common.SharedTestObjects
import kpn.api.custom.Fact
import kpn.core.util.UnitTest
import kpn.server.analyzer.engine.analysis.route.RouteNameAnalysis
import kpn.server.analyzer.engine.analysis.route.RouteNode
import kpn.server.analyzer.engine.analysis.route.RouteNodeAnalysis
import kpn.server.analyzer.engine.analysis.route.RouteNodeType
import kpn.server.analyzer.engine.analysis.route.RouteTestData
import kpn.server.analyzer.engine.analysis.route.domain.RouteAnalysisContext
import kpn.server.analyzer.engine.context.AnalysisContext

class ExpectedNameRouteAnalyzerTest extends UnitTest with SharedTestObjects {

  test("happy path") {
    val newContext = doTest(Some("01-02"), Some("01"), Some("02"))
    newContext.expectedName should equal(Some("01-02"))
    newContext.facts shouldBe empty
  }

  test("route name reversed") {
    val newContext = doTest(Some("02-01"), Some("01"), Some("02"))
    newContext.expectedName should equal(Some("01-02"))
    newContext.facts shouldBe empty
  }

  test("unexpected route name - start node does not match") {
    val newContext = doTest(Some("04-05"), Some("04"), Some("06"))
    newContext.expectedName should equal(Some("04-06"))
    newContext.facts should equal(Seq(Fact.RouteNodeNameMismatch))
  }

  test("unexpected route name - end node does not match") {
    val newContext = doTest(Some("04-05"), Some("04"), Some("07"))
    newContext.expectedName should equal(Some("04-07"))
    newContext.facts should equal(Seq(Fact.RouteNodeNameMismatch))
  }

  test("no fact when route name unknown") {
    val newContext = doTest(None, Some("01"), Some("02"))
    newContext.expectedName should equal(Some(""))
    newContext.facts shouldBe empty
  }

  test("no fact when start node unknown") {
    val newContext = doTest(Some("01-02"), None, Some("02"))
    newContext.expectedName should equal(Some(""))
    newContext.facts shouldBe empty
  }

  test("no fact when end node unknown") {
    val newContext = doTest(Some("01-02"), Some("01"), None)
    newContext.expectedName should equal(Some(""))
    newContext.facts shouldBe empty
  }

  test("preserve white space arround separator dash") {
    val newContext = doTest(Some("aaa - bbb-ccc"), Some("aaa"), Some("bbb-ccc"))
    newContext.expectedName should equal(Some("aaa - bbb-ccc"))
    newContext.facts shouldBe empty
  }

  private def doTest(routeName: Option[String], startNodeName: Option[String], endNodeName: Option[String]): RouteAnalysisContext = {
    val routeNameAnalysis = RouteNameAnalysis(name = routeName)
    val routeNodeAnalysis = RouteNodeAnalysis(
      startNodes = startNodeName.toSeq.map(name => RouteNode(RouteNodeType.Start, name = name)),
      endNodes = endNodeName.toSeq.map(name => RouteNode(RouteNodeType.End, name = name))
    )

    val context = buildContext().
      copy(routeNameAnalysis = Some(routeNameAnalysis)).
      copy(routeNodeAnalysis = Some(routeNodeAnalysis))

    ExpectedNameRouteAnalyzer.analyze(context)
  }

  private def buildContext(): RouteAnalysisContext = {
    val data = new RouteTestData("01-02") {
      node(1001)
      node(1002)
      node(1003)
      node(1004)
      node(1005)
      node(1006)
      memberWay(101, "", 1, 2, 3)
      memberWay(102, "", 3, 4, 5)
      memberWay(103, "", 5, 6)
    }.data

    val relation = data.relations(1L)
    val analysisContext = new AnalysisContext()
    RouteAnalysisContext(analysisContext, relation)
  }
}
