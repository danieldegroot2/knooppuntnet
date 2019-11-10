package kpn.core.history

import kpn.api.common.common.Ref
import kpn.api.common.diff.TagDiffs
import kpn.api.common.diff.network.NetworkNodeDiff
import kpn.api.common.diff.network.NodeIntegrityCheckDiff
import kpn.api.common.diff.network.NodeRouteReferenceDiffs
import kpn.api.common.route.RouteInfo
import kpn.api.custom.NetworkType
import kpn.core.analysis.NetworkNodeInfo

class NetworkNodeDiffAnalyzer(networkType: NetworkType, before: NetworkNodeInfo, after: NetworkNodeInfo) {

  def diffs: NetworkNodeDiff = NetworkNodeDiff(
    connection,
    roleConnection,
    definedInNetworkRelation,
    routeReferenceDiffs,
    nodeIntegrityCheckDiff,
    tagDiffs
  )

  private val beforeNode = before.networkNode.node
  private val afterNode = after.networkNode.node

  private def connection: Option[Boolean] = {
    if (before.connection != after.connection) {
      Some(after.connection)
    }
    else {
      None
    }
  }

  private def roleConnection: Option[Boolean] = {
    if (before.roleConnection != after.roleConnection) {
      Some(after.roleConnection)
    }
    else {
      None
    }
  }

  private def definedInNetworkRelation: Option[Boolean] = {
    if (before.definedInRelation != after.definedInRelation) {
      Some(after.definedInRelation)
    }
    else {
      None
    }
  }

  private def routeReferenceDiffs: Option[NodeRouteReferenceDiffs] = {
    val beforeRouteIds = before.referencedInRoutes.map(_.id).toSet
    val afterRouteIds = after.referencedInRoutes.map(_.id).toSet

    if (beforeRouteIds != afterRouteIds) {
      val removedIds = beforeRouteIds -- afterRouteIds
      val addedIds = afterRouteIds -- beforeRouteIds
      val remainingIds = afterRouteIds intersect beforeRouteIds

      val removedRouteRefs = routeRefs(before.referencedInRoutes, removedIds)
      val addedRouteRefs = routeRefs(after.referencedInRoutes, addedIds)
      val remainingRouteRefs = routeRefs(after.referencedInRoutes, remainingIds)

      Some(NodeRouteReferenceDiffs(removedRouteRefs, addedRouteRefs, remainingRouteRefs))
    }
    else {
      None
    }
  }

  private def routeRefs(routes: Seq[RouteInfo], ids: Set[Long]): Seq[Ref] = {
    routes.filter(route => ids.contains(route.id)).map(route => Ref(route.id, route.summary.name))
  }

  private def tagDiffs: Option[TagDiffs] = {
    new TagDiffAnalyzer(beforeNode, afterNode, NodeTagDiffAnalyzer.mainTagKeys).diffs
  }

  private def nodeIntegrityCheckDiff: Option[NodeIntegrityCheckDiff] = {
    if (before.integrityCheck != after.integrityCheck) {
      Some(NodeIntegrityCheckDiff(before.integrityCheck, after.integrityCheck))
    }
    else {
      None
    }
  }
}
