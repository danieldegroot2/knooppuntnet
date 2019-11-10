package kpn.server.api.analysis

import kpn.api.common.ChangesPage
import kpn.api.common.LocationPage
import kpn.api.common.PoiPage
import kpn.api.common.ReplicationId
import kpn.api.common.changes.ChangeSetPage
import kpn.api.common.changes.filter.ChangesParameters
import kpn.api.common.network.NetworkChangesPage
import kpn.api.common.network.NetworkDetailsPage
import kpn.api.common.network.NetworkFactsPage
import kpn.api.common.network.NetworkMapPage
import kpn.api.common.network.NetworkNodesPage
import kpn.api.common.network.NetworkRoutesPage
import kpn.api.common.node.MapDetailNode
import kpn.api.common.node.NodeChangesPage
import kpn.api.common.node.NodeDetailsPage
import kpn.api.common.node.NodeMapPage
import kpn.api.common.planner.RouteLeg
import kpn.api.common.route.MapDetailRoute
import kpn.api.common.route.RouteChangesPage
import kpn.api.common.route.RouteDetailsPage
import kpn.api.common.route.RouteMapPage
import kpn.api.common.statistics.Statistics
import kpn.api.common.subset.SubsetChangesPage
import kpn.api.common.subset.SubsetFactDetailsPage
import kpn.api.common.subset.SubsetFactsPage
import kpn.api.common.subset.SubsetNetworksPage
import kpn.api.common.subset.SubsetOrphanNodesPage
import kpn.api.common.subset.SubsetOrphanRoutesPage
import kpn.api.common.tiles.ClientPoiConfiguration
import kpn.api.custom.ApiResponse
import kpn.api.custom.Fact
import kpn.api.custom.NetworkType
import kpn.api.custom.Subset
import kpn.core.gpx.GpxFile

trait AnalysisFacade {

  def nodeDetails(user: Option[String], nodeId: Long): ApiResponse[NodeDetailsPage]

  def nodeMap(user: Option[String], nodeId: Long): ApiResponse[NodeMapPage]

  def nodeChanges(user: Option[String], nodeId: Long, parameters: ChangesParameters): ApiResponse[NodeChangesPage]

  def routeDetails(user: Option[String], routeId: Long): ApiResponse[RouteDetailsPage]

  def routeMap(user: Option[String], routeId: Long): ApiResponse[RouteMapPage]

  def routeChanges(user: Option[String], routeId: Long, parameters: ChangesParameters): ApiResponse[RouteChangesPage]

  def subsetNetworks(user: Option[String], subset: Subset): ApiResponse[SubsetNetworksPage]

  def subsetFacts(user: Option[String], subset: Subset): ApiResponse[SubsetFactsPage]

  def subsetOrphanNodes(user: Option[String], subset: Subset): ApiResponse[SubsetOrphanNodesPage]

  def subsetOrphanRoutes(user: Option[String], subset: Subset): ApiResponse[SubsetOrphanRoutesPage]

  def subsetChanges(user: Option[String], parameters: ChangesParameters): ApiResponse[SubsetChangesPage]

  def networkDetails(user: Option[String], id: Long): ApiResponse[NetworkDetailsPage]

  def networkMap(user: Option[String], networkId: Long): ApiResponse[NetworkMapPage]

  def networkFacts(user: Option[String], id: Long): ApiResponse[NetworkFactsPage]

  def networkNodes(user: Option[String], id: Long): ApiResponse[NetworkNodesPage]

  def networkRoutes(user: Option[String], id: Long): ApiResponse[NetworkRoutesPage]

  def networkChanges(user: Option[String], parameters: ChangesParameters): ApiResponse[NetworkChangesPage]

  // TODO not used anymore? have to re-implement? cleanup?
  def gpx(user: Option[String], networkId: Long): Option[GpxFile]

  def overview(user: Option[String]): ApiResponse[Statistics]

  def subsetFactDetails(user: Option[String], subset: Subset, fact: Fact): ApiResponse[SubsetFactDetailsPage]

  def changeSet(user: Option[String], changeSetId: Long, replicationId: Option[ReplicationId]): ApiResponse[ChangeSetPage]

  def changes(user: Option[String], parameters: ChangesParameters): ApiResponse[ChangesPage]

  def mapDetailNode(user: Option[String], networkType: NetworkType, nodeId: Long): ApiResponse[MapDetailNode]

  def mapDetailRoute(user: Option[String], routeId: Long): ApiResponse[MapDetailRoute]

  def poiConfiguration(user: Option[String]): ApiResponse[ClientPoiConfiguration]

  def poi(user: Option[String], elementType: String, elementId: Long): ApiResponse[PoiPage]

  def leg(user: Option[String], networkType: NetworkType, legId: String, sourceNodeId: String, sinkNodeId: String): ApiResponse[RouteLeg]

  def location(user: Option[String], networkType: NetworkType): ApiResponse[LocationPage]

}
