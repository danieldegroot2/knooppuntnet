package kpn.core.mongo.tools.validation

import kpn.api.custom.Subset
import kpn.core.mongo.Database
import kpn.core.mongo.actions.subsets.MongoQuerySubsetOrphanNodes
import kpn.core.mongo.actions.subsets.MongoQuerySubsetOrphanRoutes

class ValidateSubsetQueries(database: Database) {

  def validate(): Seq[ValidationResult] = {
    Seq(
      validateSubsetOrphanNodes(),
      validateSubsetOrphanRoutes()
    )
  }

  private def validateSubsetOrphanNodes(): ValidationResult = {
    ValidationResult.validate("MongoQuerySubsetOrphanNodes") {
      val nodeInfos = new MongoQuerySubsetOrphanNodes(database).execute(Subset.deBicycle)
      if (nodeInfos.size < 10) {
        Some(s"less than 10 bicycle orphan nodes in Germany (${nodeInfos.size})")
      }
      else {
        None
      }
    }
  }

  private def validateSubsetOrphanRoutes(): ValidationResult = {
    ValidationResult.validate("MongoQuerySubsetOrphanRoutes") {
      val orphanRouteInfos = new MongoQuerySubsetOrphanRoutes(database).execute(Subset.deBicycle)
      if (orphanRouteInfos.size < 100) {
        Some(s"less than 100 bicycle orphan routes in Germany (${orphanRouteInfos.size})")
      }
      else {
        None
      }
    }
  }
}
