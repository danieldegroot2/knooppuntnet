package kpn.database.actions.networks

import kpn.api.common.SharedTestObjects
import kpn.api.custom.Timestamp
import kpn.core.test.TestSupport.withDatabase
import kpn.core.util.UnitTest
import kpn.database.actions.statistics.ChangeSetCounts
import kpn.database.base.Database

class MongoQueryNetworkChangeCountsTest extends UnitTest with SharedTestObjects {

  test("execute") {

    withDatabase { database =>

      change(database, 1, 1, 2020, 1, 1, happy = false)
      change(database, 2, 1, 2021, 1, 1, happy = false)
      change(database, 3, 1, 2021, 1, 2, happy = false)
      change(database, 4, 1, 2021, 1, 3, happy = true)
      change(database, 5, 1, 2021, 2, 1, happy = false)
      change(database, 6, 2, 2021, 2, 1, happy = false)

      val query = new MongoQueryNetworkChangeCounts(database)

      query.execute(1L, 2021, None).shouldMatchTo(
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

      query.execute(1L, 2021, Some(1)).shouldMatchTo(
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
    networkId: Long,
    year: Int,
    month: Int,
    day: Int,
    happy: Boolean
  ): Unit = {
    database.networkInfoChanges.save(
      newNetworkInfoChange(
        key = newChangeKey(
          replicationNumber = replicationNumber,
          timestamp = Timestamp(year, month, day)
        ),
        networkId = networkId,
        happy = happy
      )
    )
  }
}
