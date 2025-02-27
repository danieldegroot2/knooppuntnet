package kpn.server.analyzer.engine.tile

import kpn.api.common.tiles.ZoomLevel
import kpn.api.custom.NetworkType
import kpn.core.util.Log
import kpn.server.analyzer.engine.tiles.TileData
import kpn.server.analyzer.engine.tiles.TileDataNodeBuilder
import kpn.server.analyzer.engine.tiles.TileDataRouteBuilder
import kpn.server.analyzer.engine.tiles.domain.Tile
import kpn.server.analyzer.engine.tiles.domain.TileDataNode
import kpn.server.analyzer.engine.tiles.domain.TileDataRoute
import kpn.server.repository.NodeRepository
import kpn.server.repository.RouteRepository
import kpn.server.repository.TaskRepository
import kpn.server.repository.TileRepository
import org.springframework.stereotype.Component

@Component
class TileUpdaterImpl(
  taskRepository: TaskRepository,
  nodeRepository: NodeRepository,
  routeRepository: RouteRepository,
  tileRepository: TileRepository,
  tileFileBuilder: TileFileBuilder,
  tileCalculator: TileCalculator,
  tileDataNodeBuilder: TileDataNodeBuilder
) extends TileUpdater {

  private val log = Log(classOf[TileUpdaterImpl])

  override def update(minZoomLevel: Int): Unit = {
    new Updater(minZoomLevel).update()
  }

  private class Updater(minZoomLevel: Int) {

    private val nodeCache = new TileDataCache[TileDataNode]()
    private val routeCache = new TileDataCache[TileDataRoute]()

    def update(): Unit = {
      val allTasks = taskRepository.all(TileTask.prefix)
      log.debug(s"processing ${allTasks.size} tasks")
      (minZoomLevel to ZoomLevel.maxZoom).foreach { zoomLevel =>
        routeCache.clear()
        val tasks = allTasks.filter(task => TileTask.zoomLevel(task) == zoomLevel)
        log.debug(s"processing ${tasks.size} tasks at zoomLevel $zoomLevel")
        tasks.foreach { task =>
          log.debug(s"processing task $task")
          processTask(task)
          taskRepository.delete(task)
        }
      }
    }

    private def processTask(task: String): Unit = {
      val tile = tileCalculator.tileNamed(TileTask.tileName(task))
      val networkType: NetworkType = TileTask.networkType(task)
      updateTile(networkType, tile)
    }

    private def updateTile(networkType: NetworkType, tile: Tile): Unit = {
      val tileDataNodes = collectTileDataNodes(networkType, tile)
      val tileDataRoutes = collectTileDataRoutes(networkType, tile)
      val tileData = TileData(networkType, tile, tileDataNodes, tileDataRoutes)
      tileFileBuilder.build(tileData)
    }

    private def collectTileDataNodes(networkType: NetworkType, tile: Tile): Seq[TileDataNode] = {
      val nodeIds = tileRepository.nodeIds(networkType, tile)
      nodeIds.flatMap { nodeId =>
        nodeCache.getOrElseUpdate(
          nodeId,
          nodeRepository.nodeTileInfoById(nodeId) match {
            case Some(tileInfoNode) => tileDataNodeBuilder.build(networkType, tileInfoNode)
            case None =>
              log.error(s"Unexpected data integrity problem: node $nodeId for tile ${networkType.name}-${tile.name} not found in database")
              None
          }
        )
      }
    }

    private def collectTileDataRoutes(networkType: NetworkType, tile: Tile): Seq[TileDataRoute] = {
      val routeIds = tileRepository.routeIds(networkType, tile)
      routeIds.flatMap { routeId =>
        routeCache.getOrElseUpdate(
          routeId,
          routeRepository.routeTileInfosById(routeId) match {
            case Some(routeTileInfo) =>
              val tileDataRoute = new TileDataRouteBuilder(tile.z).fromRouteInfo(routeTileInfo)
              if (tileDataRoute.segments.nonEmpty) {
                Some(tileDataRoute)
              }
              else {
                None
              }
            case None =>
              log.error(s"Unexpected data integrity problem: route $routeId for tile ${networkType.name}-${tile.name} not found in database")
              None
          }
        )
      }
    }
  }
}
