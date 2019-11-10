package kpn.core.db

import kpn.api.custom.Country
import kpn.api.custom.Fact
import kpn.api.custom.Subset
import kpn.api.custom.Tags
import kpn.api.custom.Timestamp
import kpn.core.database.Database
import kpn.server.repository.NetworkRepository
import kpn.server.repository.NetworkRepositoryImpl
import kpn.server.repository.NodeRepository
import kpn.server.repository.NodeRepositoryImpl
import kpn.server.repository.RouteRepository
import kpn.server.repository.RouteRepositoryImpl
import kpn.api.common.NetworkFacts
import kpn.api.common.SharedTestObjects
import kpn.api.common.network.Integrity
import kpn.api.common.network.NetworkAttributes
import kpn.api.common.network.NetworkInfo
import kpn.api.common.network.NetworkInfoDetail
import kpn.api.common.network.NetworkNodeInfo2
import kpn.api.common.network.NetworkRouteInfo
import kpn.api.common.network.NetworkShape
import kpn.api.common.route.RouteNetworkNodeInfo

class TestDocBuilder(database: Database) extends SharedTestObjects {

  private val networkRepository: NetworkRepository = new NetworkRepositoryImpl(database)
  private val nodeRepository: NodeRepository = new NodeRepositoryImpl(database)
  private val routeRepository: RouteRepository = new RouteRepositoryImpl(database)

  def networkInfoDetail(
    nodes: Seq[NetworkNodeInfo2] = Seq.empty,
    routes: Seq[NetworkRouteInfo] = Seq.empty,
    networkFacts: NetworkFacts = NetworkFacts(),
    shape: Option[NetworkShape] = None
  ): NetworkInfoDetail = {

    NetworkInfoDetail(
      nodes,
      routes,
      networkFacts,
      shape
    )
  }

  def networkRouteInfo(id: Long, name: String = "name", facts: Seq[Fact] = Seq.empty): NetworkRouteInfo = {
    NetworkRouteInfo(
      id,
      name = name,
      wayCount = 0,
      length = 0,
      role = None,
      relationLastUpdated = Timestamp(11, 8, 2015),
      lastUpdated = Timestamp(11, 8, 2015),
      facts = facts
    )
  }

  def network(
    id: Long,
    subset: Subset,
    name: String = "name",
    facts: Seq[Fact] = Seq.empty,
    detail: Option[NetworkInfoDetail] = None,
    meters: Int = 0,
    nodeCount: Int = 0,
    routeCount: Int = 0,
    active: Boolean = true,
    ignored: Boolean = false
  ): Unit = {
    val attributes = NetworkAttributes(
      id,
      Some(subset.country),
      subset.networkType,
      name,
      km = 0,
      meters = meters,
      nodeCount = nodeCount,
      routeCount = routeCount,
      brokenRouteCount = 0,
      brokenRoutePercentage = "",
      integrity = Integrity(),
      unaccessibleRouteCount = 0,
      connectionCount = 0,
      Timestamp(2015, 8, 11),
      Timestamp(2015, 8, 11),
      None
    )

    val networkInfo = NetworkInfo(
      attributes,
      active,
      Seq(),
      Seq(),
      Seq(),
      facts,
      Tags.empty,
      detail
    )

    networkRepository.save(networkInfo)
  }

  def node(
    id: Long,
    country: Country = Country.nl,
    tags: Tags = Tags.empty,
    active: Boolean = true,
    orphan: Boolean = false,
    facts: Seq[Fact] = Seq.empty
  ): Unit = {
    nodeRepository.save(
      newNodeInfo(
        id,
        country = Some(country),
        tags = tags,
        active = active,
        orphan = orphan,
        facts = facts
      )
    )
  }

  def route(
    id: Long,
    subset: Subset,
    name: String = "01-02",
    active: Boolean = true,
    orphan: Boolean = false,
    ignored: Boolean = false,
    facts: Seq[Fact] = Seq.empty
  ): Unit = {
    routeRepository.save(
      newRoute(
        id = id,
        country = Some(subset.country),
        networkType = subset.networkType,
        name = name,
        active = active,
        orphan = orphan,
        facts = facts
      )
    )
  }

  def route(
    subset: Subset,
    startNodeName: String,
    endNodeName: String,
    routeId: Long,
    startNodeId: Long,
    endNodeId: Long,
    meters: Int
  ): Unit = {

    val routeName = "%s-%s".format(startNodeName, endNodeName)
    val startNode = RouteNetworkNodeInfo(startNodeId, startNodeName, startNodeName)
    val endNode = RouteNetworkNodeInfo(endNodeId, endNodeName, endNodeName)

    val routeAnalysis = newRouteInfoAnalysis(
      startNodes = Seq(startNode),
      endNodes = Seq(endNode)
    )

    routeRepository.save(
      newRoute(
        id = routeId,
        country = Some(subset.country),
        networkType = subset.networkType,
        name = routeName,
        meters = meters,
        analysis = routeAnalysis
      )
    )
  }
}
