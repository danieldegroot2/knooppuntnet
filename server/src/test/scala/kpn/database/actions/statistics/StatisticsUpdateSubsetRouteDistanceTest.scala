package kpn.database.actions.statistics

import kpn.api.common.SharedTestObjects
import kpn.api.custom.Country
import kpn.api.custom.Country.de
import kpn.api.custom.Country.nl
import kpn.api.custom.NetworkType
import kpn.api.custom.NetworkType.cycling
import kpn.api.custom.NetworkType.hiking
import kpn.core.doc.Label
import kpn.core.test.TestSupport.withDatabase
import kpn.core.util.UnitTest
import kpn.database.base.Database
import kpn.server.analyzer.engine.analysis.post.StatisticsUpdater

class StatisticsUpdateSubsetRouteDistanceTest extends UnitTest with SharedTestObjects {

  test("execute") {
    withDatabase { database =>

      buildRoute(database, 11L, nl, hiking, 1000)
      buildRoute(database, 12L, nl, hiking, 2000)
      buildRoute(database, 13L, nl, cycling, 3000)
      buildRoute(database, 14L, de, hiking, 4000)
      buildRoute(database, 15L, de, hiking, 5000)
      buildRoute(database, 16L, de, cycling, 6000)
      buildRoute(database, 17L, de, cycling, 7000, active = false)

      new StatisticsUpdater(database).execute()
      val counts = new MongoQueryStatistics(database).execute()

      counts should contain(
        StatisticLongValues(
          "Distance",
          Seq(
            StatisticLongValue(de, cycling, 6L),
            StatisticLongValue(de, hiking, 9L),
            StatisticLongValue(nl, cycling, 3L),
            StatisticLongValue(nl, hiking, 3L),
          )
        )
      )
    }
  }

  private def buildRoute(database: Database, routeId: Long, country: Country, networkType: NetworkType, meters: Int, active: Boolean = true): Unit = {
    database.routes.save(
      newRouteDoc(
        newRouteSummary(
          routeId,
          Some(country),
          networkType,
          meters = meters
        ),
        labels = if (active) Seq(Label.active) else Seq.empty
      )
    )
  }
}
