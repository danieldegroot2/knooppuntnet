package kpn.server.monitor.route.update

import kpn.api.common.SharedTestObjects
import kpn.api.common.monitor.MonitorRouteUpdate
import kpn.api.common.monitor.MonitorRouteUpdateStatusCommand
import kpn.api.common.monitor.MonitorRouteUpdateStatusMessage
import kpn.api.custom.Timestamp
import kpn.core.test.TestSupport.withDatabase
import kpn.core.util.UnitTest

class MonitorUpdaterTest17_route_not_found extends UnitTest with SharedTestObjects {

  test("update/upload - route not found") {

    withDatabase() { database =>

      val configuration = MonitorUpdaterTestSupport.configuration(database)

      val group = newMonitorGroup("group-name")
      configuration.monitorGroupRepository.saveGroup(group)

      val reporter = new MonitorUpdateReporterMock()
      configuration.monitorRouteUpdateExecutor.execute(
        MonitorUpdateContext(
          "user",
          reporter,
          MonitorRouteUpdate(
            action = "update",
            groupName = "group-name",
            routeName = "unknown-route-name",
            description = Some("description"),
            comment = Some("comment"),
            relationId = Some(1),
            referenceType = "osm",
            referenceTimestamp = Some(Timestamp(2022, 8, 11)),
          )
        )
      )

      reporter.messages.shouldMatchTo(
        Seq(
          MonitorRouteUpdateStatusMessage(
            commands = Seq(
              MonitorRouteUpdateStatusCommand("step-add", "prepare"),
              MonitorRouteUpdateStatusCommand("step-add", "analyze-route-structure"),
              MonitorRouteUpdateStatusCommand("step-active", "prepare")
            )
          ),
          MonitorRouteUpdateStatusMessage(
            exception = Some("""Could not find route with name "unknown-route-name" in group "group-name"""")
          )
        )
      )
    }
  }
}
