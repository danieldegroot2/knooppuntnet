package kpn.core.tools

import kpn.core.db.couch.Couch
import kpn.core.db.couch.Database
import kpn.core.db.views.ViewRow
import kpn.core.repository.RouteRepositoryImpl
import kpn.shared.Country
import kpn.shared.NetworkType
import spray.json.JsValue

object DuplicateRoutesReport {
  def main(args: Array[String]): Unit = {
    Couch.executeIn("master1") { database =>
      new DuplicateRoutesReport(database).run()
    }
    println("Done")
  }
}

class DuplicateRoutesReport(database: Database) {

  case class RouteWays(country: Country, networkType: NetworkType, id: Long, name: String, wayIds: Set[Long])

  case class Overlap(name: String, routeId1: Long, routeId2: Long)
  implicit def overlapOrdering: Ordering[Overlap] = Ordering.by(o => (o.name, o.routeId1, o.routeId2))

  def run(): Unit = {

    val routeIds = allRouteIds()
    val routes = loadRoutes(routeIds)

    Country.all.foreach { country =>
      NetworkType.all.foreach { networkType =>
        val subsetRoutes = routes.filter(_.country == country).filter(_.networkType == networkType)
        val overlaps = findOverlaps(subsetRoutes)
        if (overlaps.nonEmpty) {
          println()
          println(s"### ${country.domain}/${networkType.name} ${subsetRoutes.size} routes, with ${overlaps.size} overlaps")
          println()
          printTableHeader()
          overlaps.sorted.foreach(printOverlap)
        }
      }
    }
  }

  private def printTableHeader(): Unit ={
    println("|name|route 1|route 2|")
    println("|----|-------|-------|")
  }

  private def printOverlap(overlap: Overlap): Unit = {
    println(s"|${overlap.name}|${link(overlap.routeId1)}|${link(overlap.routeId2)}|")
  }

  private def link(routeId: Long): String = {
    s"[$routeId](http://knooppuntnet.nl/en/route/$routeId)"
  }

  private def findOverlaps(subsetRoutes: Seq[RouteWays]): Seq[Overlap] = {
    subsetRoutes.groupBy(_.name).flatMap { case (name, routes) =>
      routes.combinations(2).flatMap { case Seq(route1, route2) =>
        if (overlap(route1, route2)) {
          Some(Overlap(name, route1.id, route2.id))
        }
        else {
          None
        }
      }
    }.toSeq
  }

  private def allRouteIds(): Seq[Long] = {

    def toRouteId(row: JsValue): Long = {
      val docId = new ViewRow(row).id.toString
      val routeId = docId.drop("route:".length + 1).dropRight(1)
      routeId.toLong
    }

    val request = """_design/AnalyzerDesign/_view/DocumentView?startkey="route"&endkey="route:a"&reduce=false&stale=ok"""
    database.getRows(request).map(toRouteId)
  }

  private def loadRoutes(routeIds: Seq[Long]): Seq[RouteWays] = {
    val routeRepository = new RouteRepositoryImpl(database)
    routeIds.zipWithIndex.flatMap { case (routeId, index) =>
      if (index % 100 == 0) {
        println(s"${routeIds.size}/$index")
      }
      routeRepository.routeWithId(routeId).flatMap { routeInfo =>
        val country = routeInfo.summary.country
        val networkType = routeInfo.summary.networkType
        val name = routeInfo.summary.name
        val wayIds = routeInfo.analysis.toSeq.flatMap(_.members.filter(_.isWay).map(_.id)).toSet
        if (routeInfo.active && routeInfo.display && !routeInfo.ignored && wayIds.nonEmpty && country.isDefined) {
          Some(RouteWays(country.get, networkType, routeInfo.id, name, wayIds))
        }
        else {
          None
        }
      }
    }
  }

  private def overlap(route1: RouteWays, route2: RouteWays): Boolean = {
    route1.name == route2.name && route1.wayIds.exists(route2.wayIds.contains)
  }

}
