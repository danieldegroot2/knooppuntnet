package kpn.database.actions.nodes

import kpn.database.actions.nodes.MongoQueryNodes.log
import kpn.database.base.Count
import kpn.database.base.Database
import kpn.core.doc.Label
import kpn.core.doc.NodeDoc
import kpn.core.util.Log
import org.mongodb.scala.model.Accumulators.sum
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.group
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Aggregates.unwind
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Filters.in
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.model.Projections.fields
import org.mongodb.scala.model.Projections.include

object MongoQueryNodes {
  private val log = Log(classOf[MongoQueryNodes])
}

// TODO MONGO cleanup - no longer used?
class MongoQueryNodes(database: Database) {

  def execute(nodeIds: Seq[Long]): Seq[NodeDoc] = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          and(
            equal("labels", Label.active),
            in("_id", nodeIds: _*)
          ),
        )
      )
      val nodes = database.nodes.aggregate[NodeDoc](pipeline, log)
      (s"nodes: ${nodes.size}", nodes)
    }
  }

  def factCount(nodeIds: Seq[Long]): Long = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          and(
            equal("labels", Label.active),
            in("_id", nodeIds: _*)
          )
        ),
        unwind("$facts"), // TODO MONGO facts related to different scopedNetworkTypes than the network for which we do this query will also be counted
        group(
          "$facts",
          sum("count", 1)
        ),
        project(
          fields(
            excludeId(),
            include("count")
          )
        )
      )
      val counts = database.nodes.aggregate[Count](pipeline, log)
      (s"factCount", counts.map(_.count).sum)
    }
  }
}
