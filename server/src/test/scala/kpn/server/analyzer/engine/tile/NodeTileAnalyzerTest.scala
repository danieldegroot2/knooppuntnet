package kpn.server.analyzer.engine.tile

import kpn.api.common.LatLon
import kpn.api.common.LatLonImpl
import kpn.core.tiles.TestTile
import kpn.core.tiles.TestTileSetup
import org.scalatest.FunSuite
import org.scalatest.Matchers

class NodeTileAnalyzerTest extends FunSuite with Matchers {

  val t = new TestTileSetup()

  val analyzer = new NodeTileAnalyzerImpl(t.tileCalculator)

  test("test") {

    val essen = LatLonImpl("51.46774", "4.46839")
    assertTile(essen, Seq(t.t22))

    val delta = 0.0005

    val centerTile = t.t22.tile.bounds
    val xCenter = centerTile.xCenter
    val yCenter = centerTile.yCenter

    val left = centerTile.xMin + delta
    val right = centerTile.xMax - delta
    val top = centerTile.yMax - delta
    val bottom = centerTile.yMin + delta

    assertTile(LatLonImpl.from(yCenter, left), Seq(t.t12, t.t22))
    assertTile(LatLonImpl.from(yCenter, right), Seq(t.t22, t.t32))
    assertTile(LatLonImpl.from(top, xCenter), Seq(t.t21, t.t22))
    assertTile(LatLonImpl.from(bottom, xCenter), Seq(t.t22, t.t23))

    assertTile(LatLonImpl.from(top, left), Seq(t.t11, t.t21, t.t12, t.t22))
    assertTile(LatLonImpl.from(top, right), Seq(t.t21, t.t31, t.t22, t.t32))
    assertTile(LatLonImpl.from(bottom, left), Seq(t.t12, t.t22, t.t13, t.t23))
    assertTile(LatLonImpl.from(bottom, right), Seq(t.t22, t.t32, t.t23, t.t33))
  }

  private def assertTile(latLon: LatLon, expected: Seq[TestTile]): Unit = {
    analyzer.tiles(t.zoomLevel, latLon).map(_.name).map(t.tilesByName).map(_.id).toSet should equal(expected.map(_.id).toSet)
  }
}
