package kpn.core.engine.analysis.country

import kpn.core.changes.RelationAnalyzer
import kpn.shared.Country
import kpn.shared.LatLon

class CountryAnalyzerMock(relationAnalyzer: RelationAnalyzer) extends CountryAnalyzerAbstract(relationAnalyzer) {

  override def countries(latLon: LatLon): Seq[Country] = {
    if (latLon.latitude == "" || latLon.latitude == "0") {
      Seq(Country.nl)
    }
    else {
      Seq()
    }
  }

  override def country(latLons: Iterable[LatLon]): Option[Country] = {
    doCountry(latLons)
  }
}
