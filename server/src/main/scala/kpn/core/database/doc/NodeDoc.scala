package kpn.core.database.doc

import kpn.api.common.NodeInfo

case class NodeDoc(_id: String, node: NodeInfo, _rev: Option[String] = None) extends Doc
