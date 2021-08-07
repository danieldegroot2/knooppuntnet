package kpn.core.mongo.actions.statistics

import kpn.api.common.statistics.StatisticValues
import kpn.core.mongo.Database
import kpn.core.mongo.actions.statistics.MongoQueryStatistics.groupValues
import kpn.core.mongo.actions.statistics.StatisticsUpdateSubsetNetworkCount.log
import kpn.core.mongo.util.MongoQuery
import kpn.core.util.Log
import org.mongodb.scala.Document
import org.mongodb.scala.model.Accumulators.sum
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.group
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Filters.exists

object StatisticsUpdateSubsetNetworkCount extends MongoQuery {
  private val log = Log(classOf[StatisticsUpdateSubsetNetworkCount])
}

class StatisticsUpdateSubsetNetworkCount(database: Database) {
  def execute(): Unit = {
    log.debugElapsed {
      val select = Seq(
        filter(
          and(
            equal("active", true),
            exists("country"),
            exists("summary.networkType")
          )
        ),
        group(
          Document(
            "country" -> "$country",
            "networkType" -> "$summary.networkType"
          ),

          sum("value", 1)
        )
      )
      val pipeline = select ++ groupValues(database, "NetworkCount")
      val values = database.networkInfos.aggregate[StatisticValues](pipeline)
      (s"${values.size} values", ())
    }
  }
}
