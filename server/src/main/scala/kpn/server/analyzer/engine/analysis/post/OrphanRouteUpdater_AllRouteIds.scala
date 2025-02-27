package kpn.server.analyzer.engine.analysis.post

import kpn.core.doc.Label
import kpn.core.util.Log
import kpn.database.base.Database
import kpn.database.base.Id
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Filters.exists
import org.mongodb.scala.model.Projections.fields
import org.mongodb.scala.model.Projections.include

class OrphanRouteUpdater_AllRouteIds(database: Database, log: Log) {

  def execute(): Seq[Long] = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          and(
            equal("labels", Label.active),
            exists("summary.country")
          )
        ),
        project(
          fields(
            include("_id")
          )
        )
      )
      val ids = database.routes.aggregate[Id](pipeline, log)
      (s"${ids.size} routes in total", ids.map(_._id))
    }
  }
}
