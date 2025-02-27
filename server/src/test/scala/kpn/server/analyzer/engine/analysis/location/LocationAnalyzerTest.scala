package kpn.server.analyzer.engine.analysis.location

import kpn.api.common.LatLon
import kpn.api.common.SharedTestObjects
import kpn.api.custom.Country
import kpn.core.util.UnitTest

object LocationAnalyzerTest {

  private var locationAnalyzerOption: Option[LocationAnalyzer] = None

  def locationAnalyzer: LocationAnalyzer = {
    if (locationAnalyzerOption.isEmpty) {
      locationAnalyzerOption = Some(new LocationAnalyzerImpl(true))
    }
    locationAnalyzerOption.get
  }
}

class LocationAnalyzerTest extends UnitTest with SharedTestObjects {

  private val locationAnalyzer = LocationAnalyzerTest.locationAnalyzer

  private val be1 = node("51.47464736069959", "4.478302001953125")
  private val be2 = node("51.43563788497879", "4.941433668136596")
  private val nl1 = node("51.48170361107213", "4.4769287109375")
  private val nl2 = node("51.43948683099483", "4.931525588035583")
  private val de1 = node("50.36999258287717", "6.7291259765625")
  private val es1 = node("40.4166314", "-3.7038148") // Madrid
  private val es2 = node("41.6564984", "-0.8787286") // Zaragoza
  private val unknown1 = node("1", "1")
  private val unknown2 = node("2", "2")

  test("regular nodes") {
    locationAnalyzer.countries(be1) should equal(Seq(Country.be))
    locationAnalyzer.countries(nl1) should equal(Seq(Country.nl))
    locationAnalyzer.countries(de1) should equal(Seq(Country.de))
    locationAnalyzer.countries(es1) should equal(Seq(Country.es))
    locationAnalyzer.countries(es2) should equal(Seq(Country.es))
  }

  test("outer 1 Baarle Nassau") {
    locationAnalyzer.countries(node("51.43581846832453", "4.926767349243164")) should equal(Seq(Country.be))
  }

  test("outer 2 Baarle Nassau") {
    locationAnalyzer.countries(node("51.43563788497879", "4.941433668136596")) should equal(Seq(Country.be))
  }

  test("inner in be outer Baarle Nassau") {
    locationAnalyzer.countries(node("51.43948683099483", "4.931525588035583")) should equal(Seq(Country.nl))
  }

  test("country be1") {
    locationAnalyzer.country(Seq(be1)) should equal(Some(Country.be))
  }

  test("country nl1") {
    locationAnalyzer.country(Seq(nl1)) should equal(Some(Country.nl))
  }

  test("country be1, be2, nl1") {
    locationAnalyzer.country(Seq(be1, be2, nl1)) should equal(Some(Country.be))
  }

  test("country be1, nl1, nl2") {
    locationAnalyzer.country(Seq(be1, nl1, nl2)) should equal(Some(Country.nl))
  }

  test("country be1, be2, nl1, nl2") {
    locationAnalyzer.country(Seq(be1, be2, nl1, nl2)) should equal(Some(Country.be))
  }

  test("country nl1, nl2, be1, be2") {
    locationAnalyzer.country(Seq(nl1, nl2, be1, be2)) should equal(Some(Country.be))
  }

  test("country unknown1") {
    locationAnalyzer.country(Seq(unknown1)) should equal(None)
  }

  test("country be1, unknown1n unknwon2") {
    locationAnalyzer.country(Seq(be1, unknown1, unknown2)) should equal(Some(Country.be))
  }

  test("Paris") {
    locationAnalyzer.countries(node("48.8568537", "2.3411688")) should equal(Seq(Country.fr))
  }

  test("Vienna") {
    locationAnalyzer.countries(node("48.12", "16.22")) should equal(Seq(Country.at))
  }

  private def node(latitude: String, longitude: String): LatLon = {
    newRawNode(latitude = latitude, longitude = longitude)
  }
}
