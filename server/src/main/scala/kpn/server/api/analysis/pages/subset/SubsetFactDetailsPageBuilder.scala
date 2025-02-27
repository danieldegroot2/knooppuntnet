package kpn.server.api.analysis.pages.subset

import kpn.api.common.subset.SubsetFactDetailsPage
import kpn.api.custom.Fact
import kpn.api.custom.Subset
import kpn.server.repository.FactRepository
import kpn.server.repository.SubsetRepository
import org.springframework.stereotype.Component

@Component
class SubsetFactDetailsPageBuilder(
  subsetRepository: SubsetRepository,
  factRepository: FactRepository
) {

  def build(subset: Subset, fact: Fact): SubsetFactDetailsPage = {
    val subsetInfo = subsetRepository.subsetInfo(subset)
    val networkFactRefss = factRepository.factsPerNetwork(subset, fact)
    val postProcessedNetworks = networkFactRefss.map { networkFactRefs =>
      if (networkFactRefs.networkName == "NodesInOrphanRoutes" || networkFactRefs.networkName == "OrphanNodes") {
        networkFactRefs.copy(factRefs = networkFactRefs.factRefs.distinct)
      }
      else {
        networkFactRefs
      }
    }

    SubsetFactDetailsPage(subsetInfo, fact, postProcessedNetworks)
  }
}
