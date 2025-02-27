package kpn.server.api.analysis.pages

import kpn.api.common.Language
import kpn.api.common.location.LocationsPage
import kpn.api.custom.Country
import kpn.api.custom.NetworkType

trait LocationsPageBuilder {
  def build(language: Language, networkType: NetworkType, country: Country): Option[LocationsPage]
}
