package kpn.server.monitor.route

import kpn.api.common.SharedTestObjects
import kpn.api.common.monitor.MonitorRouteUpdate
import kpn.api.custom.Timestamp
import kpn.core.common.Time
import kpn.core.test.TestSupport.withDatabase
import kpn.core.util.UnitTest
import org.scalatest.BeforeAndAfterEach

class MonitorUpdaterTest10_multi_gpx_delete_gpx extends UnitTest with BeforeAndAfterEach with SharedTestObjects {

  override def afterEach(): Unit = {
    Time.clear()
  }

  ignore("add route with gpx references per subrelation - delete subrelation gpx reference") {

    withDatabase() { database =>

      val configuration = MonitorUpdaterTestSupport.configuration(database)

      val group = newMonitorGroup("group")
      configuration.monitorGroupRepository.saveGroup(group)

      val route = newMonitorRoute(
        group._id,
        name = "route",
        relationId = Some(1),
        user = "user",
        referenceType = "osm",
        referenceTimestamp = Some(Timestamp(2022, 8, 11)),
        referenceFilename = None,
        relation = Some(
          newMonitorRouteRelation(
            relationId = 1,
            name = "super-route",
            happy = true,
            relations = Seq(
              newMonitorRouteRelation(
                relationId = 11,
                name = "sub-route-11",
                referenceTimestamp = Some(Timestamp(2022, 8, 11)),
                referenceFileName = Some("filename-11"),
                referenceDistance = 11,
                happy = true,
                relations = Seq(
                  newMonitorRouteRelation(
                    relationId = 111,
                    name = "sub-route-111",
                    referenceTimestamp = Some(Timestamp(2022, 8, 11)),
                    referenceFileName = Some("filename-111"),
                    referenceDistance = 1110,
                    deviationDistance = 111,
                    deviationCount = 3,
                    happy = true,
                  ),
                  newMonitorRouteRelation(
                    relationId = 112,
                    name = "sub-route-112",
                    referenceTimestamp = Some(Timestamp(2022, 8, 11)),
                    referenceFileName = Some("filename-112"),
                    referenceDistance = 1120,
                    deviationDistance = 112,
                    deviationCount = 5,
                    happy = true,
                  )
                )
              )
            )
          )
        )
      )
      val reference11 = newMonitorRouteReference(
        routeId = route._id,
        relationId = Some(11),
        referenceType = "gpx",
        referenceTimestamp = Timestamp(2022, 8, 11),
        filename = Some("filename-11"),
      )
      val reference111 = newMonitorRouteReference(
        routeId = route._id,
        relationId = Some(111),
        referenceType = "gpx",
        referenceTimestamp = Timestamp(2022, 8, 11),
        filename = Some("filename-111"),
      )
      val reference112 = newMonitorRouteReference(
        routeId = route._id,
        relationId = Some(112),
        referenceType = "gpx",
        referenceTimestamp = Timestamp(2022, 8, 11),
        filename = Some("filename-112"),
      )

      val state11 = newMonitorRouteState(
        route._id,
        11,
        timestamp = Timestamp(2022, 8, 11),
      )
      val state111 = newMonitorRouteState(
        route._id,
        111,
        timestamp = Timestamp(2022, 8, 11),
      )
      val state112 = newMonitorRouteState(
        route._id,
        112,
        timestamp = Timestamp(2022, 8, 11),
      )

      configuration.monitorGroupRepository.saveGroup(group)
      configuration.monitorRouteRepository.saveRoute(route)
      configuration.monitorRouteRepository.saveRouteReference(reference11)
      configuration.monitorRouteRepository.saveRouteReference(reference111)
      configuration.monitorRouteRepository.saveRouteReference(reference112)
      configuration.monitorRouteRepository.saveRouteState(state11)
      configuration.monitorRouteRepository.saveRouteState(state111)
      configuration.monitorRouteRepository.saveRouteState(state112)


      val update = MonitorRouteUpdate(
        action = "gpx-delete",
        groupName = group.name,
        routeName = "route-name",
        referenceType = "multi-gpx",
        relationId = Some(111),
      )

      Time.set(Timestamp(2022, 8, 11, 12, 0, 0))
      val reporter = new MonitorUpdateReporterMock()
      configuration.monitorUpdater.update("user", update, reporter)

      val updatedRoute = configuration.monitorRouteRepository.routeByName(group._id, "route").get
      val subrelation111 = updatedRoute.relation.get.relations.head.relations.head

      subrelation111.referenceTimestamp should equal(None)
      subrelation111.referenceFilename should equal(None)
      subrelation111.deviationDistance should equal(0)
      subrelation111.deviationCount should equal(0)

      val subrelation112 = updatedRoute.relation.get.relations.head.relations(1)

      subrelation112.referenceTimestamp should equal(Some(Timestamp(2022, 8, 11)))
      subrelation112.referenceFilename should equal(Some("filename-112"))
      subrelation112.deviationDistance should equal(112)
      subrelation112.deviationCount should equal(5)

      configuration.monitorRouteRepository.routeRelationReference(route._id, 11) should equal(Some(reference11))
      configuration.monitorRouteRepository.routeState(route._id, 11) should equal(Some(state11))

      configuration.monitorRouteRepository.routeRelationReference(route._id, 111) should equal(None)
      configuration.monitorRouteRepository.routeState(route._id, 111) should equal(None)

      configuration.monitorRouteRepository.routeRelationReference(route._id, 112) should equal(Some(reference112))
      configuration.monitorRouteRepository.routeState(route._id, 112) should equal(Some(state112))
    }
  }
}
