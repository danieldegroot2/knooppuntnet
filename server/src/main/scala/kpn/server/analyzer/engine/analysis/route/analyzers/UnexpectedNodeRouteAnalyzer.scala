package kpn.server.analyzer.engine.analysis.route.analyzers

import kpn.api.common.data.Node
import kpn.api.custom.Fact.RouteUnexpectedNode
import kpn.server.analyzer.engine.analysis.route.domain.RouteAnalysisContext

object UnexpectedNodeRouteAnalyzer extends RouteAnalyzer {
  def analyze(context: RouteAnalysisContext): RouteAnalysisContext = {
    new UnexpectedNodeRouteAnalyzer(context).analyze
  }
}

class UnexpectedNodeRouteAnalyzer(context: RouteAnalysisContext) {

  def analyze: RouteAnalysisContext = {
    val unexpectedNodeIds = findUnexpectedNodeIds
    context.copy(unexpectedNodeIds = Some(unexpectedNodeIds)).withFact(unexpectedNodeIds.nonEmpty, RouteUnexpectedNode)
  }

  private def findUnexpectedNodeIds: Seq[Long] = {
    // TODO ROUTE could/should move isUnexpectedNode into this class???
    routeNodes.filter(n => context.analysisContext.isUnexpectedNode(context.networkType, n)).map(_.id)
  }

  private def routeNodes: Seq[Node] = {
    context.loadedRoute.relation.nodeMembers.map(_.node)
  }
}
