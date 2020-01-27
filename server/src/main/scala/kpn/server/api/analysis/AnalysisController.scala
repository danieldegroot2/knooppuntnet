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
import kpn.api.custom.Statistics
import kpn.api.custom.Subset
import kpn.server.analyzer.engine.poi.PoiRef
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AnalysisController(analysisFacade: AnalysisFacade) {

  @GetMapping(value = Array("/json-api/overview"))
  def overview(): ApiResponse[Statistics] = {
    analysisFacade.overview(Option.apply("vmarc"))
  }

  @GetMapping(value = Array("/json-api/{country:be|de|fr|nl|at}/{networkType:cycling|hiking|horse-riding|motorboat|canoe|inline-skating}/networks"))
  def subsetNetworks(
    @PathVariable country: String,
    @PathVariable networkType: String
  ): ApiResponse[SubsetNetworksPage] = {
    val subset = Subset.ofName(country, networkType)
    analysisFacade.subsetNetworks(Option.apply("vmarc"), subset.get)
  }

  @GetMapping(value = Array("/json-api/{country:be|de|fr|nl|at}/{networkType:cycling|hiking|horse-riding|motorboat|canoe|inline-skating}/facts"))
  def subsetFacts(
    @PathVariable country: String,
    @PathVariable networkType: String
  ): ApiResponse[SubsetFactsPage] = {
    val subset = Subset.ofName(country, networkType)
    analysisFacade.subsetFacts(Option.apply("vmarc"), subset.get)
  }

  @GetMapping(value = Array("/json-api/{country:be|de|fr|nl|at}/{networkType:cycling|hiking|horse-riding|motorboat|canoe|inline-skating}/{fact}"))
  def subsetFactDetails(
    @PathVariable country: String,
    @PathVariable networkType: String,
    @PathVariable fact: String
  ): ApiResponse[SubsetFactDetailsPage] = {
    val subset = Subset.ofName(country, networkType).get // TODO improve
    val f = Fact.withName(fact).get // TODO improve
    analysisFacade.subsetFactDetails(Option.apply("vmarc"), subset, f)
  }

  @GetMapping(value = Array("/json-api/{country:be|de|fr|nl|at}/{networkType:cycling|hiking|horse-riding|motorboat|canoe|inline-skating}/orphan-nodes"))
  def subsetOrphanNodes(
    @PathVariable country: String,
    @PathVariable networkType: String
  ): ApiResponse[SubsetOrphanNodesPage] = {
    val subset = Subset.ofName(country, networkType)
    analysisFacade.subsetOrphanNodes(Option.apply("vmarc"), subset.get)
  }

  @GetMapping(value = Array("/json-api/{country:be|de|fr|nl|at}/{networkType:cycling|hiking|horse-riding|motorboat|canoe|inline-skating}/orphan-routes"))
  def subsetOrphanRoutes(
    @PathVariable country: String,
    @PathVariable networkType: String
  ): ApiResponse[SubsetOrphanRoutesPage] = {
    val subset = Subset.ofName(country, networkType)
    analysisFacade.subsetOrphanRoutes(Option.apply("vmarc"), subset.get)
  }

  @PostMapping(value = Array("/json-api/{country:be|de|fr|nl|at}/{networkType:cycling|hiking|horse-riding|motorboat|canoe|inline-skating}/changes"))
  def subsetChanges(
    @PathVariable country: String,
    @PathVariable networkType: String,
    @RequestBody parameters: ChangesParameters
  ): ApiResponse[SubsetChangesPage] = {
    val p = parameters.copy(subset = Subset.ofName(country, networkType))
    analysisFacade.subsetChanges(Option.apply("vmarc"), p)
  }

  @GetMapping(value = Array("/json-api/network/{networkId}"))
  def networkDetails(
    @PathVariable networkId: Long
  ): ApiResponse[NetworkDetailsPage] = {
    analysisFacade.networkDetails(Option.apply("vmarc"), networkId)
  }

  @GetMapping(value = Array("/json-api/network/{networkId}/map"))
  def networkMap(
    @PathVariable networkId: Long
  ): ApiResponse[NetworkMapPage] = {
    analysisFacade.networkMap(Option.apply("vmarc"), networkId)
  }

  @GetMapping(value = Array("/json-api/network/{networkId}/facts"))
  def networkFacts(
    @PathVariable networkId: Long
  ): ApiResponse[NetworkFactsPage] = {
    analysisFacade.networkFacts(Option.apply("vmarc"), networkId)
  }

  @GetMapping(value = Array("/json-api/network/{networkId}/nodes"))
  def networkNodes(
    @PathVariable networkId: Long
  ): ApiResponse[NetworkNodesPage] = {
    analysisFacade.networkNodes(Option.apply("vmarc"), networkId)
  }

  @GetMapping(value = Array("/json-api/network/{networkId}/routes"))
  def networkRoutes(
    @PathVariable networkId: Long
  ): ApiResponse[NetworkRoutesPage] = {
    analysisFacade.networkRoutes(Option.apply("vmarc"), networkId)
  }

  @PostMapping(value = Array("/json-api/network/{networkId}/changes"))
  def networkChanges(
    @PathVariable networkId: Long,
    @RequestBody parameters: ChangesParameters
  ): ApiResponse[NetworkChangesPage] = {
    val p = parameters.copy(networkId = Some(networkId))
    analysisFacade.networkChanges(Option.apply("vmarc"), p)
  }

  @GetMapping(value = Array("/json-api/node/{nodeId}"))
  def node(
    @PathVariable nodeId: Long
  ): ApiResponse[NodeDetailsPage] = {
    analysisFacade.nodeDetails(Option.apply("vmarc"), nodeId)
  }

  @GetMapping(value = Array("/json-api/node/{nodeId}/map"))
  def nodeMap(
    @PathVariable nodeId: Long
  ): ApiResponse[NodeMapPage] = {
    analysisFacade.nodeMap(Option.apply("vmarc"), nodeId)
  }

  @PostMapping(value = Array("/json-api/node/{nodeId}/Changes"))
  def nodeChanges(
    @PathVariable nodeId: Long,
    @RequestBody parameters: ChangesParameters
  ): ApiResponse[NodeChangesPage] = {
    val p = parameters.copy(nodeId = Some(nodeId))
    analysisFacade.nodeChanges(Option.apply("vmarc"), nodeId, p)
  }

  @GetMapping(value = Array("/json-api/route/{routeId}"))
  def route(
    @PathVariable routeId: Long
  ): ApiResponse[RouteDetailsPage] = {
    analysisFacade.routeDetails(Option.apply("vmarc"), routeId)
  }

  @GetMapping(value = Array("/json-api/route/{routeId}/map"))
  def routeMap(
    @PathVariable routeId: Long
  ): ApiResponse[RouteMapPage] = {
    analysisFacade.routeMap(Option.apply("vmarc"), routeId)
  }

  @PostMapping(value = Array("/json-api/route/{routeId}/changes"))
  def routeChanges(
    @PathVariable routeId: Long,
    @RequestBody parameters: ChangesParameters
  ): ApiResponse[RouteChangesPage] = {
    val p = parameters.copy(routeId = Some(routeId))
    analysisFacade.routeChanges(Option.apply("vmarc"), routeId, p)
  }

  @PostMapping(value = Array("/json-api/changes"))
  def changes(
    @RequestBody parameters: ChangesParameters
  ): ApiResponse[ChangesPage] = {
    analysisFacade.changes(user(), parameters)
  }

  @GetMapping(value = Array("/json-api/changeset/{changeSetId}/{replicationNumber}"))
  def changeSet(
    @PathVariable changeSetId: Long,
    @PathVariable replicationNumber: Int
  ): ApiResponse[ChangeSetPage] = {
    val replicationId = ReplicationId(replicationNumber)
    analysisFacade.changeSet(Option.apply("vmarc"), changeSetId, Some(replicationId))
  }

  @GetMapping(value = Array("/json-api/node-detail/{nodeId}/{networkType}"))
  def mapDetailNode(
    @PathVariable networkType: String,
    @PathVariable nodeId: Long
  ): ApiResponse[MapDetailNode] = {
    val networkTypeValue = NetworkType.withName(networkType).get
    analysisFacade.mapDetailNode(Option.apply("vmarc"), networkTypeValue, nodeId)
  }

  @GetMapping(value = Array("/json-api/route-detail/{routeId}"))
  def mapDetailRoute(
    @PathVariable routeId: Long
  ): ApiResponse[MapDetailRoute] = {
    analysisFacade.mapDetailRoute(Option.apply("vmarc"), routeId)
  }

  @GetMapping(value = Array("/json-api/poi-configuration"))
  def poiConfiguration(
  ): ApiResponse[ClientPoiConfiguration] = {
    analysisFacade.poiConfiguration(Option.apply("vmarc"))
  }

  @GetMapping(value = Array("/json-api/poi/{elementType}/{elementId}"))
  def poi(
    @PathVariable elementType: String,
    @PathVariable elementId: Long
  ): ApiResponse[PoiPage] = {
    analysisFacade.poi(Option.apply("vmarc"), PoiRef(elementType, elementId))
  }

  @GetMapping(value = Array("/json-api/leg/{networkType}/{legId}/{sourceNodeId}/{sinkNodeId}"))
  def leg(
    @PathVariable networkType: String,
    @PathVariable legId: String,
    @PathVariable sourceNodeId: String,
    @PathVariable sinkNodeId: String
  ): ApiResponse[RouteLeg] = {
    analysisFacade.leg(Option.apply("vmarc"), NetworkType.withName(networkType).get, legId, sourceNodeId, sinkNodeId)
  }

  @GetMapping(value = Array("/json-api/location/{networkType}"))
  def location(
    @PathVariable networkType: String
  ): ApiResponse[LocationPage] = {
    analysisFacade.location(Option.apply("vmarc"), NetworkType.withName(networkType).get)
  }

  private def user(): Option[String] = {
    val authentication = SecurityContextHolder.getContext.getAuthentication()
    if (authentication != null && authentication.getName != "anonymousUser") {
      Some(authentication.getName)
    }
    else {
      None
    }
  }
}
