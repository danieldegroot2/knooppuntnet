package kpn.database.actions.routes

import kpn.database.base.Database
import kpn.database.base.Id
import kpn.core.doc.Label
import kpn.core.util.Log
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.fields
import org.mongodb.scala.model.Projections.include

object MongoQueryRouteIds {
  private val log = Log(classOf[MongoQueryRouteIds])
}

class MongoQueryRouteIds(database: Database) {

  def execute(log: Log = MongoQueryRouteIds.log): Seq[Long] = {
    log.debugElapsed {
      val pipeline = Seq(
        filter(
          equal("labels", Label.active)
        ),
        project(
          fields(
            include("_id")
          )
        )
      )
      val ids = database.routes.aggregate[Id](pipeline, log).map(_._id)
      (s"${ids.size} routes", ids)
    }
  }
}
