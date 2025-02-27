package kpn.server.analyzer.engine.analysis.route.analyzers

import kpn.api.custom.Fact
import kpn.api.custom.Fact.RouteAnalysisFailed
import kpn.api.custom.Fact.RouteNodeMissingInWays
import kpn.api.custom.Fact.RouteNotBackward
import kpn.api.custom.Fact.RouteNotContinious
import kpn.api.custom.Fact.RouteNotForward
import kpn.api.custom.Fact.RouteNotOneWay
import kpn.api.custom.Fact.RouteOneWay
import kpn.api.custom.Fact.RouteUnusedSegments
import kpn.api.custom.Fact.RouteWithoutNodes
import kpn.api.custom.Fact.RouteWithoutWays
import kpn.server.analyzer.engine.analysis.route.RouteNodeAnalysis
import kpn.server.analyzer.engine.analysis.route.RouteStructure
import kpn.server.analyzer.engine.analysis.route.domain.RouteAnalysisContext
import kpn.server.analyzer.engine.analysis.route.segment.Fragment
import kpn.server.analyzer.engine.analysis.route.segment.SegmentAnalyzer
import kpn.server.analyzer.engine.analysis.route.segment.SegmentBuilder
import kpn.server.analyzer.engine.analysis.route.segment.SegmentFinderAbort
import kpn.server.analyzer.engine.context.PreconditionMissingException

import scala.collection.mutable.ListBuffer

object RouteStructureAnalyzer extends RouteAnalyzer {
  def analyze(context: RouteAnalysisContext): RouteAnalysisContext = {
    if (context.structure.isEmpty) {
      new RouteStructureAnalyzer(context).analyze
    }
    else {
      context
    }
  }
}

class RouteStructureAnalyzer(context: RouteAnalysisContext) {

  private val fragmentMap = context.fragmentMap.getOrElse(throw new PreconditionMissingException)

  private val facts: ListBuffer[Fact] = ListBuffer[Fact]()
  facts ++= context.facts

  def analyze: RouteAnalysisContext = {
    val structure = analyzeStructure(context.routeNodeAnalysis.get)
    analyzeStructure2(context.routeNodeAnalysis.get, structure, fragmentMap.all)
    context.copy(structure = Some(structure), facts = facts.toSeq)
  }

  private def analyzeStructure(routeNodeAnalysis: RouteNodeAnalysis): RouteStructure = {

    if (isAnalysisImpossible(routeNodeAnalysis)) {
      RouteStructure(
        unusedSegments = new SegmentBuilder(context.networkType, fragmentMap).segments(fragmentMap.ids)
      )
    }
    else {
      try {
        new SegmentAnalyzer(
          context.scopedNetworkType.networkType,
          context.relation.id,
          context.routeNameAnalysis.exists(_.isStartNodeNameSameAsEndNodeName),
          fragmentMap,
          routeNodeAnalysis
        ).structure
      }
      catch {
        case e: SegmentFinderAbort =>
          facts += RouteAnalysisFailed
          RouteStructure()
      }
    }
  }

  private def analyzeStructure2(routeNodeAnalysis: RouteNodeAnalysis, structure: RouteStructure, fragments: Seq[Fragment]): Unit = {
    if (!Seq(RouteAnalysisFailed, RouteWithoutNodes, RouteNodeMissingInWays).exists(facts.contains)) {
      if (!context.connection || routeNodeAnalysis.hasStartAndEndNode) {
        if (!facts.contains(RouteWithoutWays)) {
          // do not report this fact if route has no ways or is known to be incomplete

          if (routeNodeAnalysis.freeNodes.nonEmpty) {
            if (structure.unusedSegments.nonEmpty) {
              facts += RouteUnusedSegments
            }
          }
          else {

            val oneWayRouteForward = context.relation.tags.has("direction", "forward")
            val oneWayRouteBackward = context.relation.tags.has("direction", "backward")

            val oneWayRoute = context.relation.tags.tags.exists { tag =>
              (tag.key == "comment" && tag.value.contains("to be used in one direction")) ||
                (tag.key == "oneway" && tag.value == "yes") ||
                (tag.key == "signed_direction" && tag.value == "yes")
            }

            val hasValidForwardPath = structure.forwardPath.isDefined && !structure.forwardPath.exists(_.broken)
            val hasValidBackwardPath = structure.backwardPath.isDefined && !structure.backwardPath.exists(_.broken)

            if (hasValidForwardPath) {
              if (hasValidBackwardPath) {
                if (oneWayRoute || oneWayRouteForward || oneWayRouteBackward) {
                  facts += RouteNotOneWay
                }
              }
              else {
                if (oneWayRoute || oneWayRouteForward) {
                  facts += RouteOneWay
                }
                else {
                  facts += RouteNotBackward
                }
              }
            }
            else if (hasValidBackwardPath) {
              if (oneWayRoute || oneWayRouteBackward) {
                facts += RouteOneWay
              }
              else {
                facts += RouteNotForward
              }
            }
            else {
              if (oneWayRoute || oneWayRouteForward || oneWayRouteBackward) {
                facts += RouteNotOneWay
              }
              facts += RouteNotForward
              facts += RouteNotBackward
            }

            if (!Seq(RouteNodeMissingInWays, RouteOneWay).exists(facts.contains)) {
              if (structure.forwardPath.isEmpty || structure.forwardPath.get.broken ||
                structure.backwardPath.isEmpty || structure.backwardPath.get.broken) {
                facts += RouteNotContinious
              }
            }

            if (!Seq(RouteNotForward, RouteNotBackward).exists(facts.contains)) {
              if (structure.unusedSegments.nonEmpty) {
                facts += RouteUnusedSegments
              }
            }
          }
        }
      }
    }
  }

  private def isAnalysisImpossible(routeNodeAnalysis: RouteNodeAnalysis): Boolean = {
    if (context.connection && !routeNodeAnalysis.hasStartAndEndNode) {
      return true
    }
    if (facts.contains(RouteWithoutNodes)) {
      return true
    }
    if (Seq(RouteNodeMissingInWays).exists(facts.contains)) {
      // TODO ANALYSIS review this rule: RouteNodeMissingInWays means no node in ways at all, or one or more missing ???
      // TODO ANALYSIS review this rule: overlapping ways should not cause the analysis to fail ???
      return true
    }
    false
  }
}
