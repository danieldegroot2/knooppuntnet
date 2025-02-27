package kpn.server.analyzer.engine.analysis.route.segment

import kpn.api.common.SharedTestObjects
import kpn.api.custom.NetworkType
import kpn.core.util.UnitTest

class SegmentBuilderTest extends UnitTest with SharedTestObjects {

  test("no fragments") {
    assertSegments(Seq.empty)
  }

  test("two completely separate fragments") {

    val node1 = newNode(1)
    val node2 = newNode(2)
    val node3 = newNode(3)
    val node4 = newNode(4)
    val node5 = newNode(5)
    val node6 = newNode(6)

    val fragment1 = Fragment.create(None, None, newWay(10, nodes = Vector(node1, node2)), Vector.empty, None)
    val fragment2 = Fragment.create(None, None, newWay(11, nodes = Vector(node2, node3, node4)), Vector.empty, None)
    val fragment3 = Fragment.create(None, None, newWay(12, nodes = Vector(node5, node6)), Vector.empty, None)

    val fragments: Seq[Fragment] = Seq(fragment1, fragment2, fragment3)

    assertSegments(fragments, "1-2-3-4", "5-6")
  }

  test("two completely separate fragments different order") {

    val node1 = newNode(1)
    val node2 = newNode(2)
    val node3 = newNode(3)
    val node4 = newNode(4)
    val node5 = newNode(5)
    val node6 = newNode(6)

    val fragment1 = Fragment.create(None, None, newWay(10, nodes = Vector(node1, node2)), Vector.empty, None)
    val fragment2 = Fragment.create(None, None, newWay(11, nodes = Vector(node2, node3, node4)), Vector.empty, None)
    val fragment3 = Fragment.create(None, None, newWay(12, nodes = Vector(node5, node6)), Vector.empty, None)

    val fragments: Seq[Fragment] = Seq(fragment2, fragment3, fragment1)

    assertSegments(fragments, "1-2-3-4", "5-6")
  }

  test("Y fork where longest path is choosen") {

    val node1 = newNode(1)
    val node2 = newNode(2)
    val node3 = newNode(3)
    val node4 = newNode(4)
    val node5 = newNode(5)
    val node6 = newNode(6)

    val way1 = newWay(10, nodes = Vector(node1, node2, node3), length = 100)
    val way2 = newWay(11, nodes = Vector(node3, node4, node5), length = 50)
    val way3 = newWay(12, nodes = Vector(node3, node6), length = 200)

    val fragment1 = Fragment.create(way = way1)
    val fragment2 = Fragment.create(way = way2)
    val fragment3 = Fragment.create(way = way3)

    val fragments: Seq[Fragment] = Seq(fragment1, fragment2, fragment3)

    assertSegments(fragments, "1-2-3-6", "3-4-5")
  }

  private def assertSegments(fragments: Seq[Fragment], expectedSegmentNodeIds: String*): Unit = {
    val fragmentsMap = FragmentMap(fragments)
    val segments = new SegmentBuilder(NetworkType.hiking, fragmentsMap).segments(fragmentsMap.ids)
    segments.map(_.nodes.map(_.id).mkString("-")).toSet should equal(expectedSegmentNodeIds.toSet)
  }
}
