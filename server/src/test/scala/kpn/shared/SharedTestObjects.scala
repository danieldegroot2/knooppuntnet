package kpn.shared

import kpn.server.analyzer.engine.analysis.node.NodeAnalyzer
import kpn.shared.changes.Change
import kpn.shared.changes.ChangeSet
import kpn.shared.changes.details.ChangeKey
import kpn.shared.changes.details.ChangeType
import kpn.shared.changes.details.NetworkChange
import kpn.shared.changes.details.NodeChange
import kpn.shared.changes.details.RefBooleanChange
import kpn.shared.changes.details.RefChanges
import kpn.shared.changes.details.RouteChange
import kpn.shared.common.Ref
import kpn.shared.data.Node
import kpn.shared.data.Tags
import kpn.shared.data.Way
import kpn.shared.data.raw.RawMember
import kpn.shared.data.raw.RawNode
import kpn.shared.data.raw.RawRelation
import kpn.shared.data.raw.RawWay
import kpn.shared.diff.IdDiffs
import kpn.shared.diff.NetworkDataUpdate
import kpn.shared.diff.RefDiffs
import kpn.shared.diff.RouteData
import kpn.shared.diff.TagDiffs
import kpn.shared.diff.WayUpdate
import kpn.shared.diff.common.FactDiffs
import kpn.shared.diff.network.NodeRouteReferenceDiffs
import kpn.shared.diff.node.NodeMoved
import kpn.shared.diff.route.RouteDiff
import kpn.shared.network.Integrity
import kpn.shared.network.NetworkAttributes
import kpn.shared.network.NetworkInfo
import kpn.shared.network.NetworkInfoDetail
import kpn.shared.network.NetworkNodeInfo2
import kpn.shared.network.NetworkRouteInfo
import kpn.shared.route.RouteInfo
import kpn.shared.route.RouteInfoAnalysis
import kpn.shared.route.RouteMap
import kpn.shared.route.RouteMemberInfo
import kpn.shared.route.RouteNetworkNodeInfo

trait SharedTestObjects {

  val defaultTimestamp: Timestamp = Timestamp(2015, 8, 11, 0, 0, 0)

  val timestampBeforeValue = Timestamp(2015, 8, 11, 0, 0, 1)
  val timestampFromValue = Timestamp(2015, 8, 11, 0, 0, 2)
  val timestampUntilValue = Timestamp(2015, 8, 11, 0, 0, 3)
  val timestampAfterValue = Timestamp(2015, 8, 11, 0, 0, 4)

  def newRawNode(
    id: Long = 1,
    latitude: String = "0",
    longitude: String = "0",
    version: Int = 0,
    timestamp: Timestamp = defaultTimestamp,
    changeSetId: Long = 0,
    tags: Tags = Tags.empty
  ): RawNode = {
    RawNode(
      id,
      latitude,
      longitude,
      version,
      timestamp,
      changeSetId,
      tags
    )
  }

  def newRawNodeWithName(nodeId: Long, name: String, extraTags: Tags = Tags.empty): RawNode = {
    newRawNode(nodeId, tags = newNodeTags(name) ++ extraTags)
  }

  def newForeignRawNode(nodeId: Long, name: String): RawNode = {
    newRawNode(nodeId, latitude = "99", longitude = "99", tags = newNodeTags(name))
  }

  def newRawWay(
    id: Long,
    version: Int = 0,
    timestamp: Timestamp = defaultTimestamp,
    changeSetId: Long = 0,
    nodeIds: Seq[Long] = Seq.empty,
    tags: Tags = Tags.empty
  ): RawWay = {
    RawWay(
      id,
      version,
      timestamp,
      changeSetId,
      nodeIds,
      tags
    )
  }

  def newRawRelation(
    id: Long = 0,
    version: Int = 1,
    timestamp: Timestamp = defaultTimestamp,
    changeSetId: Long = 1,
    members: Seq[RawMember] = Seq.empty,
    tags: Tags = Tags.empty
  ): RawRelation = {
    RawRelation(
      id,
      version,
      timestamp,
      changeSetId,
      members,
      tags
    )
  }

  def newMember(memberType: String, ref: Long, role: String = ""): RawMember = {
    RawMember(memberType, ref, if (role.nonEmpty) Some(role) else None)
  }

  def newNetworkTags(name: String = "name"): Tags = {
    Tags.from("network" -> "rwn", "type" -> "network", "name" -> name, "network:type" -> "node_network")
  }

  def newRouteTags(name: String = ""): Tags = {
    Tags.from("network" -> "rwn", "type" -> "route", "route" -> "foot", "note" -> name, "network:type" -> "node_network")
  }

  def newNodeTags(name: String = ""): Tags = {
    Tags.from("rwn_ref" -> name, "network:type" -> "node_network")
  }

  def newChangeKey(
    replicationNumber: Int = 1,
    timestamp: Timestamp = defaultTimestamp,
    changeSetId: Long = 123,
    elementId: Long = 0
  ): ChangeKey = {
    ChangeKey(
      replicationNumber,
      timestamp,
      changeSetId,
      elementId
    )
  }

  def newRouteChange(
    key: ChangeKey = newChangeKey(),
    changeType: ChangeType = ChangeType.Create,
    name: String = "",
    addedToNetwork: Seq[Ref] = Seq.empty,
    removedFromNetwork: Seq[Ref] = Seq.empty,
    before: Option[RouteData] = None,
    after: Option[RouteData] = None,
    removedWays: Seq[RawWay] = Seq.empty,
    addedWays: Seq[RawWay] = Seq.empty,
    updatedWays: Seq[WayUpdate] = Seq.empty,
    diffs: RouteDiff = RouteDiff(),
    facts: Seq[Fact] = Seq.empty,
    happy: Boolean = false,
    investigate: Boolean = false
  ): RouteChange = {
    RouteChange(
      key,
      changeType,
      name,
      addedToNetwork,
      removedFromNetwork,
      before,
      after,
      removedWays,
      addedWays,
      updatedWays,
      diffs,
      facts,
      happy,
      investigate
    )
  }

  def newRouteData(
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    relation: RawRelation = newRawRelation(),
    name: String = "",
    networkNodes: Seq[RawNode] = Seq.empty,
    nodes: Seq[RawNode] = Seq.empty,
    ways: Seq[RawWay] = Seq.empty,
    relations: Seq[RawRelation] = Seq.empty,
    facts: Seq[Fact] = Seq.empty
  ): RouteData = {
    RouteData(
      country,
      networkType,
      relation,
      name,
      networkNodes,
      nodes,
      ways,
      relations,
      facts
    )
  }

  def newNode(
    id: Long,
    latitude: String = "0",
    longitude: String = "0",
    version: Int = 0,
    timestamp: Timestamp = defaultTimestamp,
    changeSetId: Long = 0,
    tags: Tags = Tags.empty
  ): Node = {
    Node(
      RawNode(
        id,
        latitude,
        longitude,
        version,
        timestamp,
        changeSetId,
        tags
      )
    )
  }

  def newWay(
    id: Long,
    version: Int = 0,
    timestamp: Timestamp = defaultTimestamp,
    changeSetId: Long = 0,
    nodes: Seq[Node] = Seq.empty,
    tags: Tags = Tags.empty,
    length: Int = 0
  ): Way = {
    val nodeIds = nodes.map(_.id)
    val way = RawWay(id, version, timestamp, changeSetId, nodeIds, tags)
    Way(way, nodes, length)
  }

  def newNodeInfo(
    id: Long,
    active: Boolean = true,
    orphan: Boolean = false,
    country: Option[Country] = None,
    latitude: String = "0",
    longitude: String = "0",
    lastUpdated: Timestamp = defaultTimestamp,
    tags: Tags = Tags.empty,
    facts: Seq[Fact] = Seq.empty,
    location: Option[Location] = None
  ): NodeInfo = {

    NodeInfo(
      id,
      active,
      orphan,
      country,
      NodeAnalyzer.name(tags),
      NodeAnalyzer.names(tags),
      latitude,
      longitude,
      lastUpdated,
      tags,
      facts,
      location
    )
  }

  def newRoute(
    id: Long = 0,
    active: Boolean = true,
    orphan: Boolean = false,
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    name: String = "",
    meters: Int = 0,
    wayCount: Int = 0,
    lastUpdated: Timestamp = defaultTimestamp,
    lastUpdatedBy: String = "",
    relationLastUpdated: Timestamp = defaultTimestamp,
    analysis: RouteInfoAnalysis = newRouteInfoAnalysis(),
    facts: Seq[Fact] = Seq.empty
  ): RouteInfo = {

    val summary = RouteSummary(
      id,
      country,
      networkType,
      name,
      meters,
      isBroken = false,
      wayCount,
      relationLastUpdated,
      nodeNames = Seq.empty,
      tags = Tags.empty
    )

    RouteInfo(
      summary,
      active,
      orphan,
      version = 0,
      changeSetId = 0,
      lastUpdated,
      Tags.empty,
      facts,
      Some(analysis)
    )
  }

  def newNetwork(
    id: Long,
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    name: String = "",
    active: Boolean = true,
    facts: Seq[Fact] = Seq.empty,
    networkFacts: NetworkFacts = NetworkFacts(),
    detail: Option[NetworkInfoDetail] = None,
    nodes: Seq[NetworkNodeInfo2] = Seq.empty,
    routes: Seq[NetworkRouteInfo] = Seq.empty,
    tags: Tags = Tags.empty
  ): NetworkInfo = {

    val attributes = NetworkAttributes(
      id,
      country,
      networkType,
      name,
      km = 0,
      meters = 0,
      nodeCount = nodes.size,
      routeCount = routes.size,
      brokenRouteCount = 0,
      brokenRoutePercentage = "",
      integrity = newIntegrity(),
      unaccessibleRouteCount = 0,
      connectionCount = 0,
      Timestamp(2015, 8, 11),
      Timestamp(2015, 8, 11),
      center = None
    )

    NetworkInfo(
      attributes,
      active = active,
      Seq(),
      Seq(),
      Seq(),
      facts,
      tags,
      Some(
        NetworkInfoDetail(
          nodes = nodes,
          routes = routes,
          networkFacts = networkFacts,
          shape = None
        )
      )
    )
  }

  def newNetworkAttributes(
    id: Long,
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    name: String = "",
    km: Int = 0,
    meters: Int = 0,
    nodeCount: Int = 0,
    routeCount: Int = 0,
    brokenRouteCount: Int = 0,
    brokenRoutePercentage: String = "",
    integrity: Integrity = newIntegrity(),
    unaccessibleRouteCount: Int = 0,
    connectionCount: Int = 0,
    lastUpdated: Timestamp = defaultTimestamp,
    relationLastUpdated: Timestamp = defaultTimestamp
  ): NetworkAttributes = {
    NetworkAttributes(
      id,
      country,
      networkType,
      name,
      km,
      meters,
      nodeCount,
      routeCount,
      brokenRouteCount,
      brokenRoutePercentage,
      integrity,
      unaccessibleRouteCount,
      connectionCount,
      lastUpdated,
      relationLastUpdated,
      None
    )
  }

  def newIntegrity(
    isOk: Boolean = true,
    hasChecks: Boolean = false,
    count: String = "",
    okCount: Int = 0,
    nokCount: Int = 0,
    coverage: String = "",
    okRate: String = "",
    nokRate: String = ""
  ): Integrity = {
    Integrity(
      isOk,
      hasChecks,
      count,
      okCount,
      nokCount,
      coverage,
      okRate,
      nokRate
    )
  }

  def newNetworkNodeInfo2(
    id: Long,
    title: String,
    number: String = "",
    latitude: String = "",
    longitude: String = "",
    connection: Boolean = false,
    roleConnection: Boolean = false,
    definedInRelation: Boolean = false,
    definedInRoute: Boolean = false,
    timestamp: Timestamp = defaultTimestamp,
    routeReferences: Seq[Ref] = Seq.empty,
    integrityCheck: Option[NodeIntegrityCheck] = None,
    facts: Seq[Fact] = Seq.empty,
    tags: Tags = Tags.empty
  ): NetworkNodeInfo2 = {
    NetworkNodeInfo2(
      id,
      title,
      number,
      latitude,
      longitude,
      connection,
      roleConnection,
      definedInRelation,
      definedInRoute,
      timestamp,
      routeReferences,
      integrityCheck,
      facts,
      tags
    )
  }

  def newNetworkRouteInfo(
    id: Long,
    name: String,
    wayCount: Int = 0,
    length: Int = 0, // length in meter
    role: Option[String] = None,
    relationLastUpdated: Timestamp = defaultTimestamp,
    lastUpdated: Timestamp = defaultTimestamp,
    facts: Seq[Fact] = Seq.empty
  ): NetworkRouteInfo = {
    NetworkRouteInfo(
      id,
      name,
      wayCount,
      length,
      role,
      relationLastUpdated,
      lastUpdated: Timestamp,
      facts
    )
  }

  def newRouteInfoAnalysis(
    startNodes: Seq[RouteNetworkNodeInfo] = Seq.empty,
    endNodes: Seq[RouteNetworkNodeInfo] = Seq.empty,
    startTentacleNodes: Seq[RouteNetworkNodeInfo] = Seq.empty,
    endTentacleNodes: Seq[RouteNetworkNodeInfo] = Seq.empty,
    unexpectedNodeIds: Seq[Long] = Seq.empty,
    members: Seq[RouteMemberInfo] = Seq.empty,
    tags: Tags = Tags.empty,
    expectedName: String = "",
    facts: Seq[String] = Seq.empty,
    map: RouteMap = RouteMap(),
    structureStrings: Seq[String] = Seq.empty,
    locationAnalysis: Option[RouteLocationAnalysis] = None
  ): RouteInfoAnalysis = {
    RouteInfoAnalysis(
      startNodes,
      endNodes,
      startTentacleNodes,
      endTentacleNodes,
      unexpectedNodeIds,
      members,
      expectedName,
      map,
      structureStrings,
      locationAnalysis
    )
  }

  def newRouteSummary(
    id: Long,
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    name: String = "",
    meters: Int = 0,
    isBroken: Boolean = false,
    wayCount: Int = 0,
    timestamp: Timestamp = defaultTimestamp,
    nodeNames: Seq[String] = Seq.empty,
    tags: Tags = Tags.empty
  ): RouteSummary = {
    RouteSummary(
      id,
      country,
      networkType,
      name,
      meters,
      isBroken,
      wayCount,
      timestamp,
      nodeNames,
      tags
    )
  }

  def newRouteNetworkNodeInfo(
    id: Long,
    name: String,
    alternateName: String = "",
    lat: String = "",
    lon: String = ""
  ): RouteNetworkNodeInfo = {
    RouteNetworkNodeInfo(
      id,
      name,
      alternateName,
      lat,
      lon
    )
  }

  def newChangeSet(
    id: Long = 123L,
    timestamp: Timestamp = defaultTimestamp,
    timestampFrom: Timestamp = timestampFromValue,
    timestampUntil: Timestamp = timestampUntilValue,
    timestampBefore: Timestamp = timestampBeforeValue,
    timestampAfter: Timestamp = timestampAfterValue,
    changes: Seq[Change] = Seq.empty
  ): ChangeSet = {
    ChangeSet(
      id,
      timestamp,
      timestampFrom,
      timestampUntil,
      timestampBefore,
      timestampAfter,
      changes
    )
  }

  def newNodeChange(
    key: ChangeKey = newChangeKey(),
    changeType: ChangeType = ChangeType.Update,
    subsets: Seq[Subset] = Seq.empty,
    name: String = "",
    before: Option[RawNode] = None,
    after: Option[RawNode] = None,
    connectionChanges: Seq[RefBooleanChange] = Seq.empty,
    roleConnectionChanges: Seq[RefBooleanChange] = Seq.empty,
    definedInNetworkChanges: Seq[RefBooleanChange] = Seq.empty,
    tagDiffs: Option[TagDiffs] = None,
    nodeMoved: Option[NodeMoved] = None,
    addedToRoute: Seq[Ref] = Seq.empty,
    removedFromRoute: Seq[Ref] = Seq.empty,
    addedToNetwork: Seq[Ref] = Seq.empty,
    removedFromNetwork: Seq[Ref] = Seq.empty,
    factDiffs: FactDiffs = FactDiffs(),
    facts: Seq[Fact] = Seq.empty,
    happy: Boolean = false,
    investigate: Boolean = false
  ): NodeChange = {
    NodeChange(
      key,
      changeType,
      subsets,
      name,
      before,
      after,
      connectionChanges,
      roleConnectionChanges,
      definedInNetworkChanges,
      tagDiffs,
      nodeMoved,
      addedToRoute,
      removedFromRoute,
      addedToNetwork,
      removedFromNetwork,
      factDiffs,
      facts,
      happy,
      investigate
    )
  }

  def newNetworkChange(
    key: ChangeKey = newChangeKey(),
    changeType: ChangeType = ChangeType.Update,
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    networkId: Long = 0,
    networkName: String = "",
    orphanRoutes: RefChanges = RefChanges.empty,
    orphanNodes: RefChanges = RefChanges.empty,
    networkDataUpdate: Option[NetworkDataUpdate] = None,
    networkNodes: RefDiffs = RefDiffs.empty,
    routes: RefDiffs = RefDiffs.empty,
    nodes: IdDiffs = IdDiffs.empty,
    ways: IdDiffs = IdDiffs.empty,
    relations: IdDiffs = IdDiffs.empty,
    happy: Boolean = false,
    investigate: Boolean = false
  ): NetworkChange = {
    NetworkChange(
      key,
      changeType,
      country,
      networkType,
      networkId,
      networkName,
      orphanRoutes,
      orphanNodes,
      networkDataUpdate,
      networkNodes,
      routes,
      nodes,
      ways,
      relations,
      happy,
      investigate
    )
  }

  def newRefChanges(
    oldRefs: Seq[Ref] = Seq.empty,
    newRefs: Seq[Ref] = Seq.empty
  ): RefChanges = {
    RefChanges(
      oldRefs,
      newRefs
    )
  }

  def newChangeSetSummary(
    key: ChangeKey = newChangeKey(),
    subsets: Seq[Subset] = Seq.empty,
    timestampFrom: Timestamp = timestampFromValue,
    timestampUntil: Timestamp = timestampUntilValue,
    networkChanges: NetworkChanges = NetworkChanges(),
    orphanRouteChanges: Seq[ChangeSetSubsetElementRefs] = Seq.empty,
    orphanNodeChanges: Seq[ChangeSetSubsetElementRefs] = Seq.empty,
    subsetAnalyses: Seq[ChangeSetSubsetAnalysis] = Seq.empty,
    happy: Boolean = false,
    investigate: Boolean = false
  ): ChangeSetSummary = {
    ChangeSetSummary(
      key,
      subsets,
      timestampFrom,
      timestampUntil,
      networkChanges,
      orphanRouteChanges,
      orphanNodeChanges,
      subsetAnalyses,
      happy,
      investigate
    )
  }

  def newChangeSetNetwork(
    country: Option[Country] = None,
    networkType: NetworkType = NetworkType.hiking,
    networkId: Long = 0,
    networkName: String = "",
    routeChanges: ChangeSetElementRefs = ChangeSetElementRefs.empty,
    nodeChanges: ChangeSetElementRefs = ChangeSetElementRefs.empty,
    happy: Boolean = false,
    investigate: Boolean = false
  ): ChangeSetNetwork = {
    ChangeSetNetwork(
      country,
      networkType,
      networkId,
      networkName,
      routeChanges,
      nodeChanges,
      happy,
      investigate
    )
  }

  def newChangeSetElementRef(
    id: Long,
    name: String,
    happy: Boolean = false,
    investigate: Boolean = false
  ): ChangeSetElementRef = {
    ChangeSetElementRef(
      id,
      name,
      happy,
      investigate
    )
  }

  def newNetworkInfo(
    attributes: NetworkAttributes,
    active: Boolean = true,
    nodeRefs: Seq[Long] = Seq.empty,
    routeRefs: Seq[Long] = Seq.empty,
    networkRefs: Seq[Long] = Seq.empty,
    facts: Seq[Fact] = Seq.empty,
    tags: Tags = Tags.empty,
    detail: Option[NetworkInfoDetail] = None
  ): NetworkInfo = {
    NetworkInfo(
      attributes,
      active,
      nodeRefs,
      routeRefs,
      networkRefs,
      facts,
      tags,
      detail
    )
  }

  def newRouteInfo(
    summary: RouteSummary,
    active: Boolean = true,
    orphan: Boolean = false,
    version: Int = 1,
    changeSetId: Long = 1,
    lastUpdated: Timestamp = defaultTimestamp,
    tags: Tags = Tags.empty,
    facts: Seq[Fact] = Seq.empty,
    analysis: Option[RouteInfoAnalysis] = None
  ): RouteInfo = {
    RouteInfo(
      summary,
      active,
      orphan,
      version,
      changeSetId,
      lastUpdated,
      tags,
      facts,
      analysis
    )
  }

  def newNodeRouteReferenceDiffs(
    removed: Seq[Ref] = Seq.empty,
    added: Seq[Ref] = Seq.empty,
    remaining: Seq[Ref] = Seq.empty
  ): NodeRouteReferenceDiffs = {
    NodeRouteReferenceDiffs(
      removed,
      added,
      remaining
    )
  }

  def newPoi(
    elementType: String,
    elementId: Long,
    latitude: String = "",
    longitude: String = "",
    layers: Seq[String] = Seq.empty,
    tags: Tags = Tags.empty
  ): Poi = {
    Poi(
      elementType,
      elementId,
      latitude,
      longitude,
      layers,
      tags
    )
  }

}
