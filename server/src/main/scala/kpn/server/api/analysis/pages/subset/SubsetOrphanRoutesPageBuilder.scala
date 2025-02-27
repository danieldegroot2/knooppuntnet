package kpn.server.api.analysis.pages.subset

import kpn.api.common.OrphanRouteInfo
import kpn.api.common.subset.SubsetOrphanRoutesPage
import kpn.api.custom.Fact
import kpn.api.custom.Subset
import kpn.core.doc.OrphanRouteDoc
import kpn.core.util.Log
import kpn.database.actions.subsets.MongoQuerySubsetInfo
import kpn.database.actions.subsets.MongoQuerySubsetOrphanRoutes
import kpn.database.base.Database
import kpn.server.api.analysis.pages.TimeInfoBuilder
import org.springframework.stereotype.Component

@Component
class SubsetOrphanRoutesPageBuilder(database: Database) {

  private val log = Log(classOf[SubsetOrphanRoutesPageBuilder])

  def build(subset: Subset): SubsetOrphanRoutesPage = {

    val subsetInfo = new MongoQuerySubsetInfo(database).execute(subset, log)

    val routes = new MongoQuerySubsetOrphanRoutes(database)
      .execute(subset, log)
      .map(toInfo)
      .sortBy(_.name)

    SubsetOrphanRoutesPage(
      TimeInfoBuilder.timeInfo,
      subsetInfo,
      routes
    )
  }

  private def toInfo(doc: OrphanRouteDoc): OrphanRouteInfo = {
    OrphanRouteInfo(
      id = doc._id,
      name = doc.name,
      meters = doc.meters,
      isBroken = doc.facts.contains(Fact.RouteBroken),
      accessible = !doc.facts.contains(Fact.RouteInaccessible),
      lastSurvey = doc.lastSurvey.map(_.yyyymmdd).getOrElse("-"),
      lastUpdated = doc.lastUpdated
    )
  }
}
