package kpn.api.common

object Bounds {

  def from(latLons: Seq[LatLon]): Bounds = {

    if (latLons.isEmpty) {
      Bounds()
    }
    else {
      val lattitudes = latLons.map(_.latitude.toDouble)
      val longitudes = latLons.map(_.longitude.toDouble)

      val latMin = lattitudes.min
      val lonMin = longitudes.min
      val latMax = lattitudes.max
      val lonMax = longitudes.max

      val latDelta = (latMax - latMin) / 8
      val lonDelta = (lonMax - lonMin) / 8

      Bounds(
        minLat = latMin - latDelta,
        minLon = lonMin - lonDelta,
        maxLat = latMax + latDelta,
        maxLon = lonMax + lonDelta
      )
    }
  }
}

case class Bounds(
  minLat: Double = 0,
  minLon: Double = 0,
  maxLat: Double = 0,
  maxLon: Double = 0
)
