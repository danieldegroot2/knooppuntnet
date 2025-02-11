package kpn.server.analyzer.engine.poi

import kpn.api.common.LatLon
import kpn.api.custom.Country
import kpn.core.poi.PoiLocation
import kpn.server.analyzer.engine.analysis.location.LocationAnalyzer
import org.springframework.stereotype.Component

@Component
class PoiScopeAnalyzerImpl(locationAnalyzer: LocationAnalyzer) extends PoiScopeAnalyzer {
  override def inScope(latLon: LatLon): Boolean = {
    val lat = latLon.lat
    val lon = latLon.lon
    if (PoiLocation.simpleBoundingBoxes.exists(_.contains(lon, lat))) {
      true
    }
    else if (PoiLocation.belgiumAndNetherlands.contains(lon, lat)) {
      val countries = locationAnalyzer.countries(latLon)
      countries.contains(Country.nl) || countries.contains(Country.be)
    }
    else {
      false
    }
  }
}
