package kpn.server.analyzer.engine.monitor.structure

object StructureElement {
  def from(fragments: Seq[StructureFragment], direction: Option[ElementDirection.Value]): StructureElement = {
    StructureElement(0, fragments, direction)
  }
}

case class StructureElement(
  id: Long,
  fragments: Seq[StructureFragment],
  direction: Option[ElementDirection.Value]
) {

  def startNodeId: Long = {
    fragments.head.forwardStartNodeId
  }

  def endNodeId: Long = {
    fragments.last.forwardEndNodeId
  }

  def isLoop: Boolean = {
    startNodeId == endNodeId
  }

  def nodeIds: Seq[Long] = {
    fragments.zipWithIndex.flatMap { case (fragment, index) =>
      if (index == 0) {
        fragment.nodeIds
      }
      else {
        fragment.nodeIds.tail
      }
    }
  }

  def string: String = {
    val endNodeIds = fragments.map(_.forwardEndNodeId)
    val nodeString = startNodeId.toString + endNodeIds.mkString(">", ">", "")
    val directionString = direction match {
      case None => ""
      case Some(string) => s" ($string)"
    }
    nodeString + directionString
  }
}
