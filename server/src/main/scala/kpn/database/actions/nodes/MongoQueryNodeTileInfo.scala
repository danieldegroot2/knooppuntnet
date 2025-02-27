package kpn.database.actions.nodes

import kpn.api.custom.NetworkType
import kpn.database.actions.nodes.MongoQueryNodeTileInfo.log
import kpn.database.actions.nodes.MongoQueryNodeTileInfo.projectNodeTileInfo
import kpn.database.base.Database
import kpn.core.doc.Label
import kpn.core.util.Log
import kpn.server.analyzer.engine.tiles.domain.NodeTileInfo
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.fields
import org.mongodb.scala.model.Projections.include

object MongoQueryNodeTileInfo {
  private val log = Log(classOf[MongoQueryNodeTileInfo])

  private val projectNodeTileInfo: Bson = {
    project(
      fields(
        include("_id"),
        include("names"),
        include("latitude"),
        include("longitude"),
        include("lastSurvey"),
        include("tags"),
        include("facts")
      )
    )
  }
}

class MongoQueryNodeTileInfo(database: Database) {

  def findByNetworkType(networkType: NetworkType): Seq[NodeTileInfo] = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          and(
            equal("labels", Label.active),
            equal("labels", Label.networkType(networkType))
          )
        ),
        projectNodeTileInfo
      )
      val nodes = database.nodes.aggregate[NodeTileInfo](pipeline, log)
      (s"${nodes.size} nodes", nodes)
    }
  }

  def findById(nodeId: Long): Option[NodeTileInfo] = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          and(
            equal("_id", nodeId),
            equal("labels", Label.active)
          )
        ),
        projectNodeTileInfo
      )
      val nodeOption = database.nodes.optionAggregate[NodeTileInfo](pipeline, log)
      (s"${nodeOption.size} node(s)", nodeOption)
    }
  }
}
