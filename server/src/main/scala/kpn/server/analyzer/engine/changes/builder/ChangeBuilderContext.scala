package kpn.server.analyzer.engine.changes.builder

import kpn.api.common.common.Ref
import kpn.core.analysis.Network
import kpn.server.analyzer.engine.analysis.route.RouteAnalysis
import kpn.server.analyzer.engine.changes.ChangeSetContext

case class ChangeBuilderContext(
  changeSetContext: ChangeSetContext,
  routeAnalysesBefore: Seq[RouteAnalysis],
  routeAnalysesAfter: Seq[RouteAnalysis],
  networkBefore: Option[Network],
  networkAfter: Option[Network]
) {

  def networkRef: Option[Ref] = {
    networkAfter match {
      case Some(network) => Some(network.toRef)
      case None =>
        networkBefore match {
          case Some(network) => Some(network.toRef)
          case None => None
        }
    }
  }
}
