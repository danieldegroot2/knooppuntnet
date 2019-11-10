package kpn.server.repository

import kpn.api.custom.NetworkType
import kpn.core.db.couch.Couch
import kpn.core.test.TestSupport.withDatabase
import kpn.api.common.SharedTestObjects
import kpn.api.common.common.Reference
import kpn.api.common.route.RouteReferences
import org.scalatest.FunSuite
import org.scalatest.Matchers

class RouteRepositoryTest extends FunSuite with Matchers with SharedTestObjects {

  test("routeWithId") {
    withDatabase { database =>
      val routeRepository: RouteRepository = new RouteRepositoryImpl(database)

      routeRepository.save(newRoute(10))
      routeRepository.save(newRoute(20))

      routeRepository.routeWithId(10, Couch.uiTimeout) should equal(Some(newRoute(10)))
      routeRepository.routeWithId(20, Couch.uiTimeout) should equal(Some(newRoute(20)))
      routeRepository.routeWithId(30, Couch.uiTimeout) should equal(None)
    }
  }

  test("routesWithIds") {
    withDatabase { database =>
      val routeRepository: RouteRepository = new RouteRepositoryImpl(database)
      routeRepository.save(newRoute(10))
      routeRepository.save(newRoute(20))
      routeRepository.routesWithIds(Seq(10, 20, 30), Couch.uiTimeout) should equal(Seq(newRoute(10), newRoute(20)))
    }
  }

  test("routeReferences") {

    withDatabase { database =>

      new NetworkRepositoryImpl(database).save(
        newNetwork(
          1,
          name = "network-name",
          routes = Seq(
            newNetworkRouteInfo(10, "01-02")
          )
        )
      )

      val routeRepository: RouteRepository = new RouteRepositoryImpl(database)
      routeRepository.routeReferences(10, Couch.uiTimeout, stale = false) should equal(
        RouteReferences(
          Seq(Reference(1, "network-name", NetworkType.hiking))
        )
      )
    }
  }

  test("save") {
    withDatabase { database =>
      val routeRepository: RouteRepository = new RouteRepositoryImpl(database)

      // first save
      routeRepository.save(
        newRoute(10, name = "01-02"),
        newRoute(20, name = "02-03")
      )

      routeRepository.routeWithId(10, Couch.uiTimeout) should equal(Some(newRoute(10, name = "01-02")))
      routeRepository.routeWithId(20, Couch.uiTimeout) should equal(Some(newRoute(20, name = "02-03")))
      routeRepository.routeWithId(30, Couch.uiTimeout) should equal(None)

      // save again without change
      routeRepository.save(
        newRoute(10, name = "01-02"),
        newRoute(20, name = "02-03")
      )

      routeRepository.routeWithId(10, Couch.uiTimeout) should equal(Some(newRoute(10, name = "01-02")))
      routeRepository.routeWithId(20, Couch.uiTimeout) should equal(Some(newRoute(20, name = "02-03")))
      routeRepository.routeWithId(30, Couch.uiTimeout) should equal(None)

      // update
      routeRepository.save(
        newRoute(10, name = "01-02"),
        newRoute(20, name = "02-04")
      )

      routeRepository.routeWithId(10, Couch.uiTimeout) should equal(Some(newRoute(10, name = "01-02")))
      routeRepository.routeWithId(20, Couch.uiTimeout) should equal(Some(newRoute(20, name = "02-04"))) // updated
      routeRepository.routeWithId(30, Couch.uiTimeout) should equal(None)

      // update
      routeRepository.save(
        newRoute(20, name = "02-05")
      )

      routeRepository.routeWithId(10, Couch.uiTimeout) should equal(Some(newRoute(10, name = "01-02"))) // not deleted
      routeRepository.routeWithId(20, Couch.uiTimeout) should equal(Some(newRoute(20, name = "02-05"))) // updated
      routeRepository.routeWithId(30, Couch.uiTimeout) should equal(None)
    }
  }

  test("filterKnown") {
    withDatabase { database =>
      val routeRepository: RouteRepository = new RouteRepositoryImpl(database)

      routeRepository.save(newRoute(10))
      routeRepository.save(newRoute(20))

      routeRepository.filterKnown(Set(5, 10, 15)) should equal(Set(10))
      routeRepository.filterKnown(Set(10, 20, 30)) should equal(Set(10, 20))
    }
  }

}
