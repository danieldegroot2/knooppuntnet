package kpn.database.actions.routes

import kpn.database.actions.routes.MongoQueryRouteElementIds.log
import kpn.database.base.Database
import kpn.core.doc.Label
import kpn.core.util.Log
import kpn.server.analyzer.engine.changes.changes.ReferencedElementIds
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.fields
import org.mongodb.scala.model.Projections.include

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object MongoQueryRouteElementIds {
  private val log = Log(classOf[MongoQueryRouteElementIds])
}

class MongoQueryRouteElementIds(database: Database) {

  def execute(): Seq[ReferencedElementIds] = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          equal("labels", Label.active)
        ),
        project(
          fields(
            include("elementIds")
          )
        )
      )
      val routeElementIdss = database.routes.aggregate[ReferencedElementIds](pipeline, log, duration = Duration(5, TimeUnit.MINUTES))
      (s"elementIds for active routes: ${routeElementIdss.size}", routeElementIdss)
    }
  }

}
