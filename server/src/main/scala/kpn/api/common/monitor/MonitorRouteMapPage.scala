package kpn.api.common.monitor

import kpn.api.common.Bounds

case class MonitorRouteMapPage(
  routeId: String,
  relationId: Option[Long],
  routeName: String,
  routeDescription: String,
  groupName: String,
  groupDescription: String,
  bounds: Bounds,
  osmSegments: Seq[MonitorRouteSegment],
  matchesGeometry: Option[String],
  deviations: Seq[MonitorRouteNokSegment],
  reference: Option[MonitorRouteReferenceInfo]
)
