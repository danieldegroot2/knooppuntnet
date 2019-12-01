package kpn.server.analyzer.engine.tiles.domain

import java.awt.geom.Line2D
import java.awt.geom.Point2D

case class Line(p1: Point, p2: Point) {
  def intersects(other: Line): Boolean = {
    Line2D.linesIntersect(p1.x, p1.y, p2.x, p2.y, other.p1.x, other.p1.y, other.p2.x, other.p2.y)
  }

  def length: Double = {
    Point2D.distance(p1.x, p1.y, p2.x, p2.y)
  }

  def points: Seq[Point] = Seq(p1, p2)
}
