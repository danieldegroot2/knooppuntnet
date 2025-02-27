package kpn.database.actions.routes

import kpn.api.common.SharedTestObjects
import kpn.api.custom.Timestamp
import kpn.core.test.TestSupport.withDatabase
import kpn.core.util.UnitTest
import kpn.database.actions.statistics.ChangeSetCounts
import kpn.database.base.Database

class MongoQueryRouteChangeCountsTest extends UnitTest with SharedTestObjects {

  test("execute") {

    withDatabase { database =>

      change(database, 1, 11, 2020, 1, 1, happy = false)
      change(database, 2, 11, 2021, 1, 1, happy = false)
      change(database, 3, 11, 2021, 1, 2, happy = false)
      change(database, 4, 11, 2021, 1, 3, happy = true)
      change(database, 5, 11, 2021, 2, 1, happy = false)
      change(database, 6, 12, 2021, 2, 1, happy = false)

      val query = new MongoQueryRouteChangeCounts(database)

      query.execute(11L, 2021, None).shouldMatchTo(
        ChangeSetCounts(
          years = Seq(
            newChangeSetCount(2021)(1, 4),
            newChangeSetCount(2020)(0, 1),
          ),
          months = Seq(
            newChangeSetCount(2021, 2)(0, 1),
            newChangeSetCount(2021, 1)(1, 3),
          )
        )
      )

      query.execute(11L, 2021, Some(1)).shouldMatchTo(
        ChangeSetCounts(
          years = Seq(
            newChangeSetCount(2021)(1, 4),
            newChangeSetCount(2020)(0, 1),
          ),
          months = Seq(
            newChangeSetCount(2021, 2)(0, 1),
            newChangeSetCount(2021, 1)(1, 3),
          ),
          days = Seq(
            newChangeSetCount(2021, 1, 3)(1, 1),
            newChangeSetCount(2021, 1, 2)(0, 1),
            newChangeSetCount(2021, 1, 1)(0, 1),
          )
        )
      )
    }
  }

  private def change(
    database: Database,
    replicationNumber: Int,
    routeId: Long,
    year: Int,
    month: Int,
    day: Int,
    happy: Boolean
  ): Unit = {
    database.routeChanges.save(
      newRouteChange(
        key = newChangeKey(
          replicationNumber = replicationNumber,
          timestamp = Timestamp(year, month, day),
          elementId = routeId
        ),
        happy = happy,
        impact = happy
      )
    )
  }
}
