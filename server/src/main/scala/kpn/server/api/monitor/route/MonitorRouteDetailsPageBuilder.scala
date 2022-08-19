package kpn.server.api.monitor.route

import kpn.api.common.monitor.MonitorRouteDetailsPage

trait MonitorRouteDetailsPageBuilder {

  def build(groupName: String, routeName: String): Option[MonitorRouteDetailsPage]

}
