package kpn.api.common.data

case class WayMember(way: Way, role: Option[String]) extends Member {
  override def isWay: Boolean = true
}
