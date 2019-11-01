package kpn.core.gpx

import kpn.shared.common.TrackPoint
import kpn.shared.data.Node
import kpn.shared.data.Way

import scala.collection.mutable.ListBuffer

class GpxRoute() {

  def trackSegments(ways: Seq[Way]): Seq[GpxSegment] = {
    if (ways.isEmpty) {
      Seq()
    }
    else if (ways.size == 1) {
      val trackPoints = ways.head.nodes.map(toTrackPoint)
      Seq(GpxSegment(trackPoints))
    }
    else {
      val trackSegments = ListBuffer[GpxSegment]()
      val segmentNodes = ListBuffer[Node]()
      segmentNodes ++= startNodes(ways)

      ways.tail.foreach { way =>
        val lastNode = segmentNodes.last
        val nodes = if (way.nodes.last.id == lastNode.id) way.nodes.reverse else way.nodes
        if (lastNode.id != nodes.head.id) {
          val trackPoints = segmentNodes.map(toTrackPoint)
          trackSegments += GpxSegment(trackPoints)
          segmentNodes.clear()
          segmentNodes ++= nodes
        }
        else {
          segmentNodes ++= nodes.tail
        }
      }

      if (segmentNodes.nonEmpty) {
        val trackPoints = segmentNodes.map(toTrackPoint)
        trackSegments += GpxSegment(trackPoints)
      }
      trackSegments
    }
  }

  private def toTrackPoint(node: Node) = TrackPoint(node.latitude.toString, node.longitude.toString)

  private def startNodes(ways: Seq[Way]) = {
    val nodes1 = ways.head.nodes
    val nodes2 = ways(1).nodes
    // reverse the nodes in the first way if that makes them connect with nodes in the second way
    if (nodes1.head.id == nodes2.head.id || nodes1.head.id == nodes2.last.id) nodes1.reverse else nodes1
  }
}
