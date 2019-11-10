package kpn.server.api.analysis.pages.network

import kpn.api.common.network.NetworkInfo
import kpn.api.common.network.NetworkSummary

object NetworkSummaryBuilder {

  def toSummary(networkInfo: NetworkInfo, changeCount: Int): NetworkSummary = {

    val nodeCount = networkInfo.detail match {
      case Some(detail) => detail.nodes.size
      case None => 0
    }

    val routeCount = networkInfo.detail match {
      case Some(detail) => detail.routes.size
      case None => 0
    }

    val factCount = if (networkInfo.active) {
      networkInfo.factCount
    }
    else {
      0
    }

    NetworkSummary(
      networkInfo.attributes.networkType,
      networkInfo.attributes.name,
      factCount,
      nodeCount,
      routeCount,
      changeCount,
      networkInfo.active
    )
  }
}
