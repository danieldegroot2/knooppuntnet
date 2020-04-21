package kpn.server.analyzer.engine.tile

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TileDataCacheTest extends AnyFunSuite with Matchers {

  test("cache") {
    val cache = new TileDataCache[String]()

    cache.getOrElseUpdate(1, None) should equal(None)
    cache.getOrElseUpdate(1, Some("one")) should equal(Some("one"))
    cache.getOrElseUpdate(1, None) should equal(Some("one"))

    cache.clear()
    cache.getOrElseUpdate(1, None) should equal(None)
  }
}
