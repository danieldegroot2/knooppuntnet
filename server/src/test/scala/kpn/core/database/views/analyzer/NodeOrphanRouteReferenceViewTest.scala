package kpn.core.database.views.analyzer

import kpn.api.custom.NetworkType
import kpn.core.database.Database
import kpn.core.db.couch.Couch
import kpn.core.test.TestSupport.withDatabase
import kpn.server.repository.RouteRepositoryImpl
import kpn.api.common.SharedTestObjects
import kpn.api.common.node.NodeOrphanRouteReference
import org.scalatest.FunSuite
import org.scalatest.Matchers

class NodeOrphanRouteReferenceViewTest extends FunSuite with Matchers with SharedTestObjects {

  private val timeout = Couch.uiTimeout

  test("node references in orphan route") {

    withDatabase { database =>
      val routeRepository = new RouteRepositoryImpl(database)
      routeRepository.save(
        newRoute(
          id = 10,
          orphan = true,
          networkType = NetworkType.hiking,
          name = "01-02",
          analysis = newRouteInfoAnalysis(
            startNodes = Seq(
              newRouteNetworkNodeInfo(
                id = 1001,
                name = "01"
              )
            ),
            endNodes = Seq(
              newRouteNetworkNodeInfo(
                id = 1002,
                name = "02"
              )
            ),
            startTentacleNodes = Seq(
              newRouteNetworkNodeInfo(
                id = 1003,
                name = "01"
              )
            ),
            endTentacleNodes = Seq(
              newRouteNetworkNodeInfo(
                id = 1004,
                name = "02"
              )
            )
          )
        )
      )

      val expectedReferences = Seq(
        NodeOrphanRouteReference(
          networkType = NetworkType.hiking,
          routeId = 10,
          routeName = "01-02"
        )
      )

      queryNode(database, 1001) should equal(expectedReferences)
      queryNode(database, 1002) should equal(expectedReferences)
      queryNode(database, 1003) should equal(expectedReferences)
      queryNode(database, 1004) should equal(expectedReferences)
    }
  }

  test("no node references in orphan routes") {
    withDatabase { database =>
      queryNode(database, 1001) should equal(Seq())
    }
  }

  test("node references in non-orphan routes are ignored") {
    withDatabase { database =>
      val routeRepository = new RouteRepositoryImpl(database)
      routeRepository.save(
        newRoute( // not an orphan route
          id = 10,
          analysis = newRouteInfoAnalysis(
            startNodes = Seq(
              newRouteNetworkNodeInfo(
                id = 1001,
                name = "01"
              )
            )
          )
        )
      )
      queryNode(database, 1001) should equal(Seq())
    }
  }

  test("node references in non-active orphan routes are ignored") {
    withDatabase { database =>
      val routeRepository = new RouteRepositoryImpl(database)
      routeRepository.save(
        newRoute(
          id = 10,
          orphan = true,
          active = false,
          networkType = NetworkType.hiking,
          name = "01-02",
          analysis = newRouteInfoAnalysis(
            startNodes = Seq(
              newRouteNetworkNodeInfo(
                id = 1001,
                name = "01"
              )
            )
          )
        )
      )

      queryNode(database, 1001) should equal(Seq())
    }
  }

  def queryNode(database: Database, nodeId: Long): Seq[NodeOrphanRouteReference] = {
    NodeOrphanRouteReferenceView.query(database, nodeId, stale = false)
  }

}
