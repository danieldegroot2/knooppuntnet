package kpn.core.database.views.analyzer

import kpn.api.custom.NetworkType
import kpn.core.database.Database
import kpn.core.db.couch.Couch
import kpn.core.test.TestSupport.withDatabase
import kpn.server.repository.NetworkRepositoryImpl
import kpn.api.common.NodeIntegrityCheck
import kpn.api.common.SharedTestObjects
import kpn.api.common.common.Ref
import kpn.api.common.network.NetworkInfo
import kpn.api.common.node.NodeNetworkIntegrityCheck
import kpn.api.common.node.NodeNetworkReference
import kpn.api.common.node.NodeNetworkRouteReference
import org.scalatest.FunSuite
import org.scalatest.Matchers

class NodeNetworkReferenceViewTest extends FunSuite with Matchers with SharedTestObjects {

  private val timeout = Couch.uiTimeout

  test("node network reference") {

    withDatabase { database =>
      val networkRepository = new NetworkRepositoryImpl(database)
      networkRepository.save(buildNetworkWithNode1001and1002())
      networkRepository.save(buildNetworkWithNode1001())

      queryNode(database, 1001) should equal(
        Seq(
          NodeNetworkReference(
            networkId = 1,
            networkType = NetworkType.hiking,
            networkName = "network-1",
            nodeDefinedInRelation = true,
            nodeConnection = true,
            nodeRoleConnection = true,
            nodeIntegrityCheck = None,
            facts = Seq.empty,
            routes = Seq(
              NodeNetworkRouteReference(
                routeId = 10,
                routeName = "01-02",
                routeRole = Some("connection")
              )
            )
          ),
          NodeNetworkReference(
            networkId = 2,
            networkType = NetworkType.hiking,
            networkName = "network-2",
            nodeDefinedInRelation = true,
            nodeConnection = false,
            nodeRoleConnection = false,
            nodeIntegrityCheck = None,
            facts = Seq.empty,
            routes = Seq.empty
          )
        )
      )

      queryNode(database, 1002) should equal(
        Seq(
          NodeNetworkReference(
            networkId = 1,
            networkType = NetworkType.hiking,
            networkName = "network-1",
            nodeDefinedInRelation = true,
            nodeConnection = false,
            nodeRoleConnection = false,
            nodeIntegrityCheck = Some(
              NodeNetworkIntegrityCheck(
                failed = true,
                expected = 3,
                actual = 1
              )
            ),
            facts = Seq.empty,
            routes = Seq(
              NodeNetworkRouteReference(
                routeId = 10,
                routeName = "01-02",
                routeRole = Some("connection")
              )
            )
          )
        )
      )
    }
  }

  test("no node network references when network not active") {

    withDatabase { database =>
      val networkRepository = new NetworkRepositoryImpl(database)
      networkRepository.save(buildInactiveNetwork())
      queryNode(database, 1001) should equal(Seq())
    }
  }

  def queryNode(database: Database, nodeId: Long): Seq[NodeNetworkReference] = {
    NodeNetworkReferenceView.query(database, nodeId, stale = false)
  }

  private def buildNetworkWithNode1001and1002(): NetworkInfo = {
    newNetwork(
      1,
      name = "network-1",
      nodes = Seq(
        newNetworkNodeInfo2(
          1001,
          "01",
          definedInRelation = true,
          connection = true,
          roleConnection = true,
          routeReferences = Seq(Ref(10, "01-02"))
        ),
        newNetworkNodeInfo2(
          1002,
          "02",
          definedInRelation = true,
          routeReferences = Seq(Ref(10, "01-02")),
          integrityCheck = Some(
            NodeIntegrityCheck(
              nodeName = "02",
              nodeId = 1002,
              actual = 1,
              expected = 3,
              failed = true
            )
          )
        )
      ),
      routes = Seq(
        newNetworkRouteInfo(
          10,
          "01-02",
          role = Some("connection")
        )
      )
    )
  }

  private def buildNetworkWithNode1001(): NetworkInfo = {
    newNetwork(
      2,
      name = "network-2",
      nodes = Seq(
        newNetworkNodeInfo2(
          1001,
          "01",
          definedInRelation = true
        )
      )
    )
  }

  private def buildInactiveNetwork(): NetworkInfo = {
    newNetwork(
      3,
      name = "network-3",
      active = false,
      nodes = Seq(
        newNetworkNodeInfo2(
          1001,
          "01",
          definedInRelation = true
        )
      )
    )
  }

}
