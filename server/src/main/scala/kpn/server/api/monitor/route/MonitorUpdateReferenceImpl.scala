package kpn.server.api.monitor.route

import kpn.api.base.ObjectId
import kpn.api.common.Bounds
import kpn.api.custom.{Relation, Timestamp}
import kpn.core.common.Time
import kpn.server.analyzer.engine.monitor.{MonitorFilter, MonitorRouteOsmSegmentAnalyzer}
import kpn.server.api.monitor.MonitorUtil
import kpn.server.api.monitor.domain.{MonitorRoute, MonitorRouteReference}
import org.locationtech.jts.geom.{GeometryCollection, GeometryFactory}
import org.locationtech.jts.io.geojson.GeoJsonWriter
import org.springframework.stereotype.Component

@Component
class MonitorUpdateReferenceImpl(
  monitorRouteRelationRepository: MonitorRouteRelationRepository,
  monitorRouteOsmSegmentAnalyzer: MonitorRouteOsmSegmentAnalyzer
) extends MonitorUpdateReference {

  def update(context: MonitorUpdateContext): MonitorUpdateContext = {

    context.oldRoute match {
      case None =>
        context.newRoute match {
          case None => context
          case Some(newRoute) =>
            if (newRoute.referenceType.contains("osm")) {
              updateOsmReferences(context, newRoute)
            }
            else {
              context
            }
        }

      case Some(oldRoute) =>
        context.newRoute match {
          case None => context
          case Some(newRoute) =>

            if (isOsmReferenceChanged(oldRoute, newRoute)) {
              updateOsmReferences(context, newRoute)
            }
            else if (isGpxReferenceChanged(oldRoute, newRoute)) {
              context
            }
            else {
              context
            }

        }
    }
  }

  private def isOsmReferenceChanged(oldRoute: MonitorRoute, newRoute: MonitorRoute): Boolean = {
    newRoute.referenceType.contains("osm") && (
      oldRoute.referenceType != newRoute.referenceType ||
        oldRoute.relationId != newRoute.relationId ||
        oldRoute.referenceDay != newRoute.referenceDay
      )
  }

  private def isGpxReferenceChanged(oldRoute: MonitorRoute, newRoute: MonitorRoute): Boolean = {
    throw new RuntimeException("TODO")
  }

  private def updateOsmReferences(context: MonitorUpdateContext, newRoute: MonitorRoute): MonitorUpdateContext = {

    newRoute.relation match {
      case None => // TODO single reference (not a superroute)
        newRoute.relationId match {
          case None => context // TODO add error in MonitorRouteSaveResult ???
          case Some(relationId) =>
            newRoute.referenceDay match {
              case None => context // TODO add error in MonitorRouteSaveResult ???
              case Some(referenceDay) =>
                monitorRouteRelationRepository.loadTopLevel(Some(Timestamp(referenceDay)), relationId) match {
                  case None => context // TODO add error in MonitorRouteSaveResult !!
                  case Some(relation) =>
                    val relations: Seq[Relation] = MonitorFilter.relationsInRelation(relation)
                    val references = relations.map { routeRelation =>

                      val wayMembers = MonitorFilter.filterWayMembers(routeRelation.wayMembers)
                      val bounds = Bounds.from(wayMembers.flatMap(_.way.nodes))
                      val analysis = monitorRouteOsmSegmentAnalyzer.analyze(wayMembers)

                      val geomFactory = new GeometryFactory
                      val geometryCollection = new GeometryCollection(analysis.routeSegments.map(_.lineString).toArray, geomFactory)
                      val geoJsonWriter = new GeoJsonWriter()
                      geoJsonWriter.setEncodeCRS(false)
                      val geometry = geoJsonWriter.write(geometryCollection)

                      MonitorRouteReference(
                        ObjectId(),
                        newRoute._id,
                        Some(routeRelation.id),
                        Time.now,
                        "TODO user",
                        bounds,
                        "osm",
                        newRoute.referenceDay.get,
                        analysis.osmDistance,
                        analysis.routeSegments.size,
                        None,
                        geometry
                      )
                    }

                    val referenceDistance = references.map(_.distance).sum
                    val updatedNewRoute = context.newRoute.map { route =>
                      route.copy(
                        referenceDistance = referenceDistance
                      )
                    }

                    context.copy(
                      newRoute = updatedNewRoute,
                      newReferences = references,
                    )
                }
            }
        }


      case Some(monitorRouteRelation) =>

        val references = if (monitorRouteRelation.relations.isEmpty) {

          monitorRouteRelationRepository.loadTopLevel(Some(Timestamp(newRoute.referenceDay.get /*TODO make more safe*/)), newRoute.relationId.get /*TODO make more safe*/) match {
            case None => Seq.empty // TODO add error in MonitorRouteSaveResult !!
            case Some(relation) =>

              val wayMembers = MonitorFilter.filterWayMembers(relation.wayMembers)
              val bounds = Bounds.from(wayMembers.flatMap(_.way.nodes))
              val analysis = monitorRouteOsmSegmentAnalyzer.analyze(wayMembers)

              val geomFactory = new GeometryFactory
              val geometryCollection = new GeometryCollection(analysis.routeSegments.map(_.lineString).toArray, geomFactory)
              val geoJsonWriter = new GeoJsonWriter()
              geoJsonWriter.setEncodeCRS(false)
              val geometry = geoJsonWriter.write(geometryCollection)

              Seq(
                MonitorRouteReference(
                  ObjectId(),
                  newRoute._id,
                  Some(relation.id),
                  Time.now,
                  "TODO user",
                  bounds,
                  "osm",
                  newRoute.referenceDay.get,
                  analysis.osmDistance,
                  analysis.routeSegments.size,
                  None,
                  geometry
                )
              )
          }

        }
        else {
          MonitorUtil.subRelationsIn(newRoute).flatMap { monitorRouteSubRelation =>
            newRoute.referenceDay match {
              case None => None // TODO add error in MonitorRouteSaveResult ???
              case Some(referenceDay) =>
                monitorRouteRelationRepository.loadTopLevel(Some(Timestamp(referenceDay)), monitorRouteSubRelation.relationId) match {
                  case None => None // TODO add error in MonitorRouteSaveResult !!
                  case Some(subRelation) =>

                    val wayMembers = MonitorFilter.filterWayMembers(subRelation.wayMembers)
                    val bounds = Bounds.from(wayMembers.flatMap(_.way.nodes))
                    val analysis = monitorRouteOsmSegmentAnalyzer.analyze(wayMembers)

                    val geomFactory = new GeometryFactory
                    val geometryCollection = new GeometryCollection(analysis.routeSegments.map(_.lineString).toArray, geomFactory)
                    val geoJsonWriter = new GeoJsonWriter()
                    geoJsonWriter.setEncodeCRS(false)
                    val geometry = geoJsonWriter.write(geometryCollection)

                    Some(
                      MonitorRouteReference(
                        ObjectId(),
                        newRoute._id,
                        Some(subRelation.id),
                        Time.now,
                        "TODO user",
                        bounds,
                        "osm",
                        newRoute.referenceDay.get,
                        analysis.osmDistance,
                        analysis.routeSegments.size,
                        None,
                        geometry
                      )
                    )
                }
            }
          }
        }
        context.copy(
          newReferences = context.newReferences ++ references
        )
    }
  }
}
