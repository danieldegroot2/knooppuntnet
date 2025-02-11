package kpn.database.actions.subsets

import kpn.api.custom.Subset
import kpn.core.doc.OrphanNodeDoc
import kpn.core.util.Log
import kpn.database.actions.subsets.MongoQuerySubsetOrphanNodes.log
import kpn.database.base.Database
import kpn.database.base.Id
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.computed
import org.mongodb.scala.model.Projections.fields

object MongoQuerySubsetOrphanNodes {
  private val log = Log(classOf[MongoQuerySubsetOrphanNodes])
}

class MongoQuerySubsetOrphanNodes(database: Database) {

  def execute(subset: Subset): Seq[OrphanNodeDoc] = {

    val pipeline = Seq(
      filter(
        and(
          equal("country", subset.country.domain),
          equal("networkType", subset.networkType.name),
        )
      )
    )

    log.debugElapsed {
      val docs = database.orphanNodes.aggregate[OrphanNodeDoc](pipeline, log)
      val message = s"subset ${subset.name} orphan nodes: ${docs.size}"
      (message, docs)
    }
  }

  def ids(subset: Subset): Seq[Long] = {

    val pipeline = Seq(
      filter(
        and(
          equal("country", subset.country.domain),
          equal("networkType", subset.networkType.name),
        )
      ),
      project(
        fields(
          computed("_id", "$nodeId")
        )
      )
    )

    log.debugElapsed {
      val ids = database.orphanNodes.aggregate[Id](pipeline, log).map(_._id)
      val message = s"subset ${subset.name} orphan node ids: ${ids.size}"
      (message, ids)
    }
  }
}
