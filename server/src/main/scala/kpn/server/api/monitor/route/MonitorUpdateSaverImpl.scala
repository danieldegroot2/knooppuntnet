package kpn.server.api.monitor.route

import kpn.api.common.monitor.MonitorRouteRelation
import kpn.server.repository.MonitorRouteRepository
import org.springframework.stereotype.Component

@Component
class MonitorUpdateSaverImpl(
  monitorRouteRepository: MonitorRouteRepository
) extends MonitorUpdateSaver {

  def save(originalContext: MonitorUpdateContext): MonitorUpdateContext = {

    var context: MonitorUpdateContext = originalContext

    context.newReferences.foreach { routeRelationReference =>
      monitorRouteRepository.saveRouteReference(routeRelationReference)
    }

    context.newStates.foreach { state =>
      monitorRouteRepository.saveRouteState(state)
    }

    if (context.newReferences.nonEmpty) {
      // TODO this is only needed for routes with multiple references !
      monitorRouteRepository.superRouteReferenceSummary(context.routeId) match {
        case None =>
        case Some(referenceDistance) =>
          context.newRoute match {
            case None =>
              context.oldRoute match {
                case None =>
                case Some(oldRoute) =>
                  val updatedRoute = oldRoute.copy(
                    referenceDistance = referenceDistance
                  )
                  context = context.copy(
                    newRoute = Some(updatedRoute)
                  )
              }

            case Some(newRoute) =>
              val updatedRoute = newRoute.copy(
                referenceDistance = referenceDistance
              )
              context = context.copy(
                newRoute = Some(updatedRoute)
              )
          }
      }
    }

    if (context.newStates.nonEmpty) {

      if (context.route.isSuperRoute) {
        monitorRouteRepository.superRouteStateSummary(context.routeId) match {
          case None => // cannot do update
          case Some(monitorRouteStateSummary) =>

            val relation = context.route.relation.map(relation => updatedMonitorRouteRelation(context, relation))

            val updatedRoute = context.route.copy(
              deviationDistance = monitorRouteStateSummary.deviationDistance,
              deviationCount = monitorRouteStateSummary.deviationCount,
              osmWayCount = monitorRouteStateSummary.osmWayCount,
              osmDistance = monitorRouteStateSummary.osmDistance,
              relation = relation
            )
            context = context.copy(
              newRoute = Some(updatedRoute)
            )
        }
      }
      else {
        // TODO at this point there should not be an empty oldRoute.relation!!
        val state = context.newStates.head // TODO there should be exactly 1 state, add assertion?

        val referenceDistance = context.newReferences.find(_.relationId.contains(state.relationId)) match {
          case Some(ref) => ref.distance
          case None => context.route.referenceDistance
        }

        val relation = context.route.relation.map(relation => updatedMonitorRouteRelation(context, relation))

        val updatedRoute = context.route.copy(
          deviationDistance = state.deviations.map(_.meters).sum,
          deviationCount = state.deviations.size,
          osmWayCount = state.wayCount,
          osmDistance = state.osmDistance,
          referenceDistance = referenceDistance,
          relation = relation
        )
        context = context.copy(
          newRoute = Some(updatedRoute)
        )
      }
    }

    context.newRoute match {
      case Some(route) => monitorRouteRepository.saveRoute(route)
      case None =>
    }
    context
  }

  private def updatedMonitorRouteRelation(context: MonitorUpdateContext, monitorRouteRelation: MonitorRouteRelation): MonitorRouteRelation = {

    val updatedRelations = monitorRouteRelation.relations.map(r => updatedMonitorRouteRelation(context, r))

    context.newStates.find(_.relationId == monitorRouteRelation.relationId) match {
      case None =>
        monitorRouteRelation.copy(
          relations = updatedRelations
        )

      case Some(state) =>

        monitorRouteRelation.copy(
          deviationDistance = state.deviations.map(_.meters).sum,
          deviationCount = state.deviations.size,
          osmWayCount = state.wayCount,
          osmDistance = state.osmDistance,
          osmSegmentCount = state.osmSegments.size,
          happy = state.happy,
          relations = updatedRelations
        )
    }
  }
}
