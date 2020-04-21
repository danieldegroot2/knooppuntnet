package kpn.server.analyzer.engine.analysis.caseStudies

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class Issue31 extends AnyFunSuite with Matchers {

  test("oneway:bicycle=no overrules junction=roundabout oneway") {
    val route = CaseStudy.routeAnalysis("4271").route
    route.facts should equal(Seq()) // no more RouteNotBackward etc. generated
  }

}
