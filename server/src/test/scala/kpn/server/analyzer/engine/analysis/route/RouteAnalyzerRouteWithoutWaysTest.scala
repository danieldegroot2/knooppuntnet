package kpn.server.analyzer.engine.analysis.route

import kpn.api.custom.Fact.RouteBroken
import kpn.api.custom.Fact.RouteWithoutWays
import kpn.core.util.UnitTest
import kpn.server.analyzer.engine.analysis.country.CountryAnalyzerNoop
import kpn.server.analyzer.engine.analysis.node.OldNodeAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteCountryAnalyzer
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteLocationAnalyzerMock
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteNodeInfoAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteTileAnalyzer
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.analyzer.engine.tile.RouteTileCalculatorImpl
import kpn.server.analyzer.engine.tile.TileCalculatorImpl
import kpn.server.analyzer.load.data.LoadedRoute

class RouteAnalyzerRouteWithoutWaysTest extends UnitTest {

  test("RouteNotForward and RouteNotBackward should not be reported for routes without ways") {

    val data = new RouteTestData("01-02") {
      node(1, "01")
      node(2, "02")
      memberNode(1)
      memberNode(2)
    }.data

    val loadedRoute = LoadedRoute(
      scopedNetworkType = null,
      data,
      data.relations(1L)
    )

    val analysisContext = new AnalysisContext()
    val countryAnalyzer = new CountryAnalyzerNoop()
    val tileCalculator = new TileCalculatorImpl()
    val routeTileCalculator = new RouteTileCalculatorImpl(tileCalculator)
    val routeTileAnalyzer = new RouteTileAnalyzer(routeTileCalculator)
    val routeCountryAnalyzer = new RouteCountryAnalyzer(countryAnalyzer)
    val routeLocationAnalyzer = new RouteLocationAnalyzerMock()
    val oldNodeAnalyzer = new OldNodeAnalyzerImpl()
    val routeNodeInfoAnalyzer = new RouteNodeInfoAnalyzerImpl(analysisContext, oldNodeAnalyzer)
    val routeAnalyzer = new MasterRouteAnalyzerImpl(
      analysisContext,
      routeCountryAnalyzer,
      routeLocationAnalyzer,
      routeTileAnalyzer,
      routeNodeInfoAnalyzer
    )
    val routeAnalysis = routeAnalyzer.analyze(loadedRoute, orphan = false)
    routeAnalysis.route.facts should equal(Seq(RouteWithoutWays, RouteBroken))
  }
}
