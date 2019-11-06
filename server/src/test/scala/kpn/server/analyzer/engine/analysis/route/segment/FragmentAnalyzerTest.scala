package kpn.server.analyzer.engine.analysis.route.segment

import kpn.server.analyzer.engine.analysis.route.RouteTestData
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteNameAnalyzer
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteNodeAnalyzer
import kpn.server.analyzer.engine.analysis.route.domain.RouteAnalysisContext
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.analyzer.load.data.LoadedRoute
import kpn.shared.NetworkType
import kpn.shared.data.Tags
import org.scalatest.FunSuite
import org.scalatest.Matchers

class FragmentAnalyzerTest extends FunSuite with Matchers {

  test("single way route") {

    val d = new RouteTestData("01-02") {
      node(1, "01")
      node(4, "02")
      memberNode(1)
      memberWay(10, "", 1, 2, 3, 4)
      memberNode(4)
    }

    fragments(d) should equal("<01-02 10>")
  }

  test("simple route") {

    val d = new RouteTestData("01-02") {

      node(1, "01")
      node(6, "02")
      memberNode(1)
      memberWay(10, "", 1, 2, 3)
      memberWay(11, "", 3, 4, 5)
      memberWay(12, "", 5, 6)
      memberNode(6)
    }

    fragments(d) should equal("<01- 10><11><-02 12>")
  }

  test("node in the middle of a way") {

    val d = new RouteTestData("01-02") {

      node(1, "01")
      node(4, "01")
      node(6, "02")

      memberNode(1)
      memberWay(10, "", 1, 2, 3)
      memberWay(11, "", 3, 4, 5)
      memberWay(12, "", 5, 6)
      memberNode(6)
    }

    fragments(d) should equal("<01.b- 10><-01.a 11(3-4)><01.a- 11(4-5)><-02 12>")
  }


  test("roundabout") {

    val d = new RouteTestData("01-02") {

      node(1, "01")
      node(10, "02")

      memberWay(10, "", 1, 2, 4)
      memberWay(11, Tags.from("junction" -> "roundabout"), "", 3, 4, 5, 6, 7, 8, 3)
      memberWay(12, "", 7, 9, 10)
    }

    fragments(d) should equal("<01- 10><11(3-4)><11(4-5-6-7)><11(7-8-3)><-02 12>")
  }

  test("roundabout 2") {

    val d = new RouteTestData("01-02") {

      node(8, "01")
      node(9, "02")

      memberWay(10, "", 8, 3)
      memberWay(11, Tags.from("junction" -> "roundabout"), "", 1, 2, 3, 4, 5, 6, 7, 1)
      memberWay(12, "", 6, 9)
    }

    fragments(d) should equal("<01- 10><11(1-2-3)><11(3-4-5-6)><11(6-7-1)><-02 12>")
  }

  test("roundabout 3") {

    val d = new RouteTestData("01-02") {

      node(3, "01")
      node(9, "02")

      memberWay(11, Tags.from("junction" -> "roundabout"), "", 1, 2, 3, 4, 5, 6, 7, 1)
      memberWay(12, "", 6, 9)
    }

    fragments(d) should equal("<-01 11(1-2-3)><01- 11(3-4-5-6)><11(6-7-1)><-02 12>")
  }

  test("closed loop") {

    val d = new RouteTestData("01-02") {

      node(1, "01")
      node(10, "02")

      memberWay(10, "", 1, 2, 4)
      memberWay(11, "", 3, 4, 5, 6, 7, 8, 3)
      memberWay(12, "", 7, 9, 10)
    }

    fragments(d) should equal("<01- 10><11(3-4)><11(4-5-6-7)><11(7-8-3)><-02 12>")
  }

  test("crossing ways") {

    val d = new RouteTestData("01-02") {
      memberWay(10, "", 1, 2, 3, 4, 5)
      memberWay(11, "", 6, 7, 3, 8, 9)
    }

    fragments(d) should equal("<10(1-2-3)><10(3-4-5)><11(6-7-3)><11(3-8-9)>")
  }

  test("Y fork") {

    val d = new RouteTestData("01-02") {
      memberWay(10, "", 1, 2, 3, 4, 5)
      memberWay(11, "", 3, 8, 9)
    }

    fragments(d) should equal("<10(1-2-3)><10(3-4-5)><11>")
  }

  private def fragments(d: RouteTestData): String = {
    val data = d.data
    val relation = data.relations(d.routeRelationId)
    val analysisContext = new AnalysisContext()
    val context1 = RouteAnalysisContext(
      analysisContext,
      networkNodes = Map.empty,
      loadedRoute = LoadedRoute(
        country = None,
        networkType = NetworkType.hiking,
        "",
        data = data,
        relation = relation
      ),
      orphan = false
    )
    val context2 = new RouteNameAnalyzer(context1).analyze
    val context3 = new RouteNodeAnalyzer(context2).analyze
    val fragments = new FragmentAnalyzer(context3.routeNodeAnalysis.get.usedNodes, relation.wayMembers).fragments
    fragments.map(fragment => new FragmentFormatter(fragment).string).mkString
  }
}
