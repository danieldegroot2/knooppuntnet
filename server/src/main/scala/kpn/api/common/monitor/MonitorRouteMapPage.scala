package kpn.api.common.monitor

import kpn.api.common.Bounds
import kpn.api.custom.Timestamp

case class MonitorRouteMapPage(
  relationId: Option[Long],
  routeName: String,
  routeDescription: String,
  groupName: String,
  groupDescription: String,
  referenceType: String, // "osm" | "gpx" | "multi-gpx"
  bounds: Option[Bounds],
  analysisTimestamp: Option[Timestamp],
  currentSubRelation: Option[MonitorRouteSubRelation],
  previousSubRelation: Option[MonitorRouteSubRelation],
  nextSubRelation: Option[MonitorRouteSubRelation],
  osmSegments: Seq[MonitorRouteSegment],
  matchesGeoJson: Option[String],
  deviations: Seq[MonitorRouteDeviation],
  reference: Option[MonitorRouteReferenceInfo],
  subRelations: Seq[MonitorRouteSubRelation]
)
