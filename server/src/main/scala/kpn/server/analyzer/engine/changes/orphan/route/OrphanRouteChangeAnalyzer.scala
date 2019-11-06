package kpn.server.analyzer.engine.changes.orphan.route

import kpn.server.analyzer.engine.changes.changes.ChangeSetBuilder
import kpn.server.analyzer.engine.changes.ElementChanges
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.repository.BlackListRepository
import kpn.shared.changes.ChangeAction.Create
import kpn.shared.changes.ChangeAction.Delete
import kpn.shared.changes.ChangeAction.Modify
import kpn.shared.changes.ChangeSet
import kpn.shared.data.raw.RawRelation
import org.springframework.stereotype.Component

@Component
class OrphanRouteChangeAnalyzer(
  analysisContext: AnalysisContext,
  blackListRepository: BlackListRepository
) {

  def analyze(changeSet: ChangeSet): ElementChanges = {

    val createdRelationsById = routeMap(changeSet, Create)
    val updatedRelationsById = routeMap(changeSet, Modify)
    val deletedRelationsById = routeMap(changeSet, Delete)

    val createdRouteIds = routeRelationIds(createdRelationsById)
    val updatedRouteIds = routeRelationIds(updatedRelationsById)

    val routeCreateIds1 = createdRouteIds.filterNot(isKnownRoute)
    val routeCreateIds2 = updatedRouteIds.filterNot(isKnownRoute)

    val routeUpdateIds1 = analysisContext.data.orphanRoutes.watched.referencedBy(ChangeSetBuilder.elementIdsIn(changeSet)).toSet
    val routeUpdateIds2 = updatedRouteIds.filter(isKnownOrphanRoute)

    val deletes = {
      val knownOrphanRouteDeletes = deletedRelationsById.keySet.filter(isKnownOrphanRoute)
      val knownOrphanRoutesWithRequiredTagsMissing = updatedRelationsById.values.filter(isKnownOrphanRouteWithRequiredTagsMissing).map(_.id)
      knownOrphanRouteDeletes ++ knownOrphanRoutesWithRequiredTagsMissing
    }

    val updates = routeUpdateIds1 ++ routeUpdateIds2 -- deletes
    val creates = routeCreateIds1 ++ routeCreateIds2 -- updates -- deletes

    val sortedCreates = creates.toList.sorted
    val sortedUpdates = updates.toList.sorted
    val sortedDeletes = deletes.toList.sorted

    ElementChanges(sortedCreates, sortedUpdates, sortedDeletes)
  }

  private def routeMap(changeSet: ChangeSet, action: Int): Map[Long, RawRelation] = {
    changeSet.changes.filter(_.action == action).flatMap(_.elements).collect { case e: RawRelation => e }.map(n => n.id -> n).toMap
  }

  private def routeRelationIds(relationsById: Map[Long, RawRelation]): Set[Long] = {
    relationsById.values.
      filter(analysisContext.isRouteRelation).
      filterNot(isBlackListed).
      map(_.id).
      toSet
  }

  private def isKnownOrphanRouteWithRequiredTagsMissing(relation: RawRelation): Boolean = {
    isKnownOrphanRoute(relation.id) && !analysisContext.isRouteRelation(relation)
  }

  private def isKnownRoute(routeId: Long): Boolean = {
    isKnownOrphanRoute(routeId) ||
      analysisContext.data.networks.watched.isReferencingRelation(routeId)
  }

  private def isKnownOrphanRoute(routeId: Long): Boolean = {
    analysisContext.data.orphanRoutes.watched.contains(routeId)
  }

  private def isBlackListed(relation: RawRelation): Boolean = {
    blackListRepository.get.containsRoute(relation.id)
  }
}
