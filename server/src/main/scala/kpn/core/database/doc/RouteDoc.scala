package kpn.core.database.doc

import kpn.api.common.route.RouteInfo

case class RouteDoc(_id: String, route: RouteInfo, _rev: Option[String] = None) extends Doc
