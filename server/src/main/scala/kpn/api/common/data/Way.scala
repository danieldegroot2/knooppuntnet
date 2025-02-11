package kpn.api.common.data

import kpn.api.common.data.raw.RawWay
import kpn.api.custom.Tags
import kpn.api.custom.Timestamp

case class Way(raw: RawWay, nodes: Vector[Node], length: Long /* meters */) extends Element {

  def id: Long = raw.id

  def version: Long = raw.version

  def timestamp: Timestamp = raw.timestamp

  def changeSetId: Long = raw.changeSetId

  def tags: Tags = raw.tags

  override def isWay: Boolean = true

}
