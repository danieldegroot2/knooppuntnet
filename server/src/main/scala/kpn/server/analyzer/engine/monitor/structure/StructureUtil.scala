package kpn.server.analyzer.engine.monitor.structure

object StructureUtil {

  def roundaboutNodeIds(startNodeId: Long, nodeIds: Seq[Long]): Option[Seq[Long]] = {
    val reducedNodeIds = if (nodeIds.head == nodeIds.last) {
      nodeIds.dropRight(1)
    }
    else {
      throw new Exception("roundabout is expected to have identical start and end node")
    }

    val startIndex = reducedNodeIds.indexOf(startNodeId)
    if (startIndex >= 0) {
      Some(reducedNodeIds.drop(startIndex) ++ reducedNodeIds.takeWhile(_ != startNodeId))
    }
    else {
      None
    }
  }

  def roundaboutNodeIds(startNodeId: Long, endNodeId: Long, nodeIds: Seq[Long]): Option[Seq[Long]] = {
    val reducedNodeIds = if (nodeIds.head == nodeIds.last) {
      nodeIds.dropRight(1)
    }
    else {
      throw new Exception("roundabout is expected to have identical start and end node")
    }
    val startIndex = reducedNodeIds.indexOf(startNodeId)
    if (startIndex >= 0) {
      val endIndex = reducedNodeIds.indexOf(endNodeId)
      if (endIndex >= 0) {
        if (endIndex > startIndex) {
          Some(reducedNodeIds.drop(startIndex).take(endIndex - startIndex + 1))
        }
        else if (endIndex < startIndex) {
          Some(reducedNodeIds.drop(startIndex) ++ reducedNodeIds.take(endIndex + 1))
        }
        else {
          roundaboutNodeIds(startNodeId, nodeIds)
        }
      }
      else {
        None
      }
    }
    else {
      None
    }
  }
}
