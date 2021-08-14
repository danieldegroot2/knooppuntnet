package kpn.core.tools.support

import kpn.api.custom.Tag
import kpn.api.custom.Timestamp
import kpn.core.mongo.Database
import kpn.core.mongo.util.Mongo
import kpn.core.overpass.OverpassQueryExecutorImpl
import kpn.server.analyzer.load.OldRouteLoaderImpl

object SurfaceTagsTool {
  def main(args: Array[String]): Unit = {
    Mongo.executeIn("test") { database =>
      new SurfaceTagsTool(database).analyze()
    }
  }
}

// https://wiki.openstreetmap.org/wiki/Key:surface

class SurfaceTagsTool(database: Database) {

  private val tagKeys = Seq(
    "highway",
    "bridge",
    "surface",
    "smoothness",
    "mtb:scale",
    "tracktype",
    "sac_scale",
    "sport",
    "service",
    "bicyle:dismount",
    "bicycleroute",
    "bicycle",
    "cycleway",
    "cycleway:right",
    "cycleway:left"
  )


  def analyze(): Unit = {

    val executor = new OverpassQueryExecutorImpl()
    val routeLoader = new OldRouteLoaderImpl(executor)

    val counts = new scala.collection.mutable.HashMap[String, Int]

    val routeIds = database.routes.ids() //.take(500)
    routeIds.zipWithIndex.foreach { case (routeId, index) =>

      println(s"$index/${routeIds.size}")

      val tags: Seq[Tag] = routeLoader.loadRoute(Timestamp(2018, 8, 1), routeId) match {
        case Some(loadedRoute) => loadedRoute.relation.wayMembers.flatMap(_.way.tags.tags)
        case None => Seq.empty
      }

      tags.foreach { tag =>
        if (tagKeys.contains(tag.key)) {
          val mapKey = tag.key + "=" + tag.value
          if (counts.contains(mapKey)) {
            counts.put(mapKey, counts(mapKey) + 1)
          }
          else {
            counts.put(mapKey, 1)
          }
        }
      }
    }

    counts.keys.toSeq.sorted.foreach { key =>
      println(key + "\t" + counts(key))
    }
  }
}
