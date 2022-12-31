package kpn.server.api.monitor.route

import kpn.api.base.ObjectId
import kpn.api.common.monitor.{MonitorRouteProperties, MonitorRouteSaveResult}
import kpn.api.custom.Day
import kpn.core.common.Time
import kpn.core.tools.monitor.MonitorRouteGpxReader
import kpn.core.util.Log
import kpn.server.analyzer.engine.monitor.MonitorRouteAnalysisSupport.toMeters
import kpn.server.analyzer.engine.monitor.{MonitorRouteAnalysisSupport, MonitorRouteReferenceUtil}
import kpn.server.api.monitor.domain.{MonitorGroup, MonitorRoute, MonitorRouteReference}
import kpn.server.repository.{MonitorGroupRepository, MonitorRouteRepository}
import org.springframework.stereotype.Component

import scala.xml.Elem

@Component
class MonitorUpdaterImpl(
  monitorGroupRepository: MonitorGroupRepository,
  monitorRouteRepository: MonitorRouteRepository,
  monitorUpdateRoute: MonitorUpdateRoute,
  monitorUpdateStructure: MonitorUpdateStructure,
  monitorUpdateReference: MonitorUpdateReference,
  monitorUpdateAnalyzer: MonitorUpdateAnalyzer,
  saver: MonitorUpdateSaver,
  xxx: XxxImpl,
) extends MonitorUpdater {

  def add(
    user: String,
    groupName: String,
    properties: MonitorRouteProperties
  ): MonitorRouteSaveResult = {

    Log.context(Seq("add-route", s"group=$groupName", s"route=${properties.name}")) {
      val group = findGroup(groupName)
      assertNewRoute(group, properties.name)
      var context = MonitorUpdateContext(group, properties.referenceType)
      context = monitorUpdateRoute.update(context, ObjectId(), user, properties)
      context = monitorUpdateStructure.update(context)
      context = monitorUpdateReference.update(context)
      context = monitorUpdateAnalyzer.analyze(context)
      context = saver.save(context)
      context.saveResult
    }
  }

  def update(
    user: String,
    groupName: String,
    routeName: String,
    properties: MonitorRouteProperties
  ): MonitorRouteSaveResult = {

    Log.context(Seq("route-update", s"group=$groupName", s"route=$routeName")) {
      val group = findGroup(groupName)
      val oldRoute = findRoute(group._id, routeName)
      var context = MonitorUpdateContext(group, properties.referenceType, oldRoute = Some(oldRoute))
      context = monitorUpdateRoute.update(context, oldRoute._id, user, properties)
      context = monitorUpdateStructure.update(context)
      context = monitorUpdateReference.update(context)
      context = monitorUpdateAnalyzer.analyze(context)
      context = saver.save(context)
      context.saveResult
    }
  }

  override def upload(
    user: String,
    groupName: String,
    routeName: String,
    relationId: Long,
    referenceDay: Day,
    filename: String,
    xml: Elem
  ): MonitorRouteSaveResult = {

    Log.context(Seq("route-update", s"group=$groupName", s"route=$routeName")) {
      val group = findGroup(groupName)
      val oldRoute = findRoute(group._id, routeName)
      var context = MonitorUpdateContext(group, oldRoute.referenceType, oldRoute = Some(oldRoute))

      val now = Time.now
      val geometryCollection = new MonitorRouteGpxReader().read(xml)
      val bounds = MonitorRouteAnalysisSupport.geometryBounds(geometryCollection)
      val geoJson = MonitorRouteAnalysisSupport.toGeoJson(geometryCollection)

      // TODO should delete already existing reference here?

      val referenceLineStrings = MonitorRouteReferenceUtil.toLineStrings(geometryCollection)
      val distance = Math.round(toMeters(referenceLineStrings.map(_.getLength).sum))

      val segmentCount = geometryCollection.getNumGeometries


      val reference = MonitorRouteReference(
        ObjectId(),
        routeId = oldRoute._id,
        relationId = Some(relationId),
        created = now,
        user = user,
        bounds = bounds,
        referenceType = "gpx", // "osm" | "gpx"
        referenceDay = referenceDay,
        distance = distance,
        segmentCount = segmentCount,
        filename = Some(filename),
        geometry = geoJson
      )

      context = context.copy(
        newReferences = context.newReferences ++ Seq(reference)
      )

      oldRoute.referenceType match {
        case "gpx" =>
          val gpxDistance = {
            val referenceLineStrings = MonitorRouteReferenceUtil.toLineStrings(geometryCollection)
            Math.round(toMeters(referenceLineStrings.map(_.getLength).sum))
          }

          val updatedRoute = oldRoute.copy(
            referenceFilename = reference.filename,
            referenceDistance = gpxDistance,
          )

          context = context.copy(
            newRoute = Some(updatedRoute)
          )

          context = saver.save(context)

          MonitorRouteSaveResult()

        case "multi-gpx" =>

          // TODO perform analysis of the sub-relation !!

          xxx.analyzeReference(oldRoute._id, reference) match {
            case None =>
            case Some(state) =>
              context = context.copy(
                newStates = context.newStates :+ state
              )
          }

          context = saver.save(context)

          MonitorRouteSaveResult()


        case _ =>
          MonitorRouteSaveResult()

      }
    }
  }

  private def findGroup(groupName: String): MonitorGroup = {
    monitorGroupRepository.groupByName(groupName).getOrElse {
      throw new IllegalArgumentException(
        s"""${Log.contextString} Could not find group with name "$groupName""""
      )
    }
  }

  private def findRoute(groupId: ObjectId, routeName: String): MonitorRoute = {
    monitorRouteRepository.routeByName(groupId, routeName).getOrElse {
      throw new IllegalArgumentException(
        s"""${Log.contextString} Could not find route with name "$routeName" in group "${groupId.oid}""""
      )
    }
  }

  private def assertNewRoute(group: MonitorGroup, routeName: String): Unit = {
    monitorRouteRepository.routeByName(group._id, routeName) match {
      case None =>
      case Some(route) =>
        throw new IllegalArgumentException(
          s"""${Log.contextString} Could not add route with name "$routeName": already exists (_id=${route._id.oid}) in group with name "${group.name}""""
        )
    }
  }
}
