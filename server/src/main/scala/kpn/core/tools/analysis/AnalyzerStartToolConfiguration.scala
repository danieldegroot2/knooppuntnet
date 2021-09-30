package kpn.core.tools.analysis

import kpn.api.common.ReplicationId
import kpn.api.common.changes.ChangeSet
import kpn.api.custom.Timestamp
import kpn.core.common.TimestampUtil
import kpn.database.util.Mongo
import kpn.core.overpass.OverpassQueryExecutorImpl
import kpn.core.tools.config.Dirs
import kpn.core.tools.status.StatusRepository
import kpn.core.tools.status.StatusRepositoryImpl
import kpn.server.analyzer.engine.analysis.country.CountryAnalyzerImpl
import kpn.server.analyzer.engine.analysis.location.LocationConfigurationReader
import kpn.server.analyzer.engine.analysis.location.RouteLocatorImpl
import kpn.server.analyzer.engine.analysis.network.NetworkRelationAnalyzerImpl
import kpn.server.analyzer.engine.analysis.node.NodeAnalyzer
import kpn.server.analyzer.engine.analysis.node.NodeAnalyzerImpl
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeCountryAnalyzer
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeCountryAnalyzerImpl
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeLocationsAnalyzer
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeLocationsAnalyzerImpl
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeRouteReferencesAnalyzer
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeRouteReferencesAnalyzerImpl
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeTileAnalyzer
import kpn.server.analyzer.engine.analysis.node.analyzers.NodeTileAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.MasterRouteAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteCountryAnalyzer
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteLocationAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.analyzers.RouteTileAnalyzer
import kpn.server.analyzer.engine.changes.ChangeSetContext
import kpn.server.analyzer.engine.changes.OsmChangeRepository
import kpn.server.analyzer.engine.changes.changes.ChangeSetInfoApiImpl
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.analyzer.engine.context.ElementIds
import kpn.server.analyzer.engine.context.Watched
import kpn.server.analyzer.engine.tile.NodeTileCalculatorImpl
import kpn.server.analyzer.engine.tile.RouteTileCalculatorImpl
import kpn.server.analyzer.engine.tile.TileCalculatorImpl
import kpn.server.repository.AnalysisRepository
import kpn.server.repository.AnalysisRepositoryImpl
import kpn.server.repository.ChangeSetRepositoryImpl
import kpn.server.repository.FactRepositoryImpl
import kpn.server.repository.NetworkRepositoryImpl
import kpn.server.repository.NodeRepositoryImpl
import kpn.server.repository.OrphanRepositoryImpl
import kpn.server.repository.RouteRepositoryImpl

import java.util.concurrent.Executor

class AnalyzerStartToolConfiguration(val analysisExecutor: Executor, options: AnalyzerStartToolOptions) {

  val dirs: Dirs = Dirs()

  private val mongoDatabase = Mongo.database(Mongo.client, "kpn-test")

  val analysisContext = new AnalysisContext()

  private val locationConfiguration = new LocationConfigurationReader().read()
  private val routeLocator = new RouteLocatorImpl(locationConfiguration)
  val countryAnalyzer = new CountryAnalyzerImpl()

  val networkRepository = new NetworkRepositoryImpl(mongoDatabase)
  val routeRepository = new RouteRepositoryImpl(mongoDatabase)
  val nodeRepository = new NodeRepositoryImpl(mongoDatabase)

  private val tileCalculator = new TileCalculatorImpl()
  private val nodeTileCalculator = new NodeTileCalculatorImpl(tileCalculator)

  val nodeAnalyzer: NodeAnalyzer = {
    val nodeCountryAnalyzer = new NodeCountryAnalyzerImpl(countryAnalyzer)
    val nodeTileAnalyzer = new NodeTileAnalyzerImpl(nodeTileCalculator)
    val nodeLocationsAnalyzer = new NodeLocationsAnalyzerImpl(locationConfiguration, true)
    val nodeRouteReferencesAnalyzer = new NodeRouteReferencesAnalyzerImpl(nodeRepository)
    new NodeAnalyzerImpl(
      nodeCountryAnalyzer: NodeCountryAnalyzer,
      nodeTileAnalyzer: NodeTileAnalyzer,
      nodeLocationsAnalyzer: NodeLocationsAnalyzer,
      nodeRouteReferencesAnalyzer: NodeRouteReferencesAnalyzer
    )
  }
  private val routeTileCalculator = new RouteTileCalculatorImpl(tileCalculator)
  private val routeTileAnalyzer = new RouteTileAnalyzer(routeTileCalculator)

  val analysisRepository: AnalysisRepository = new AnalysisRepositoryImpl(mongoDatabase)

  val statusRepository: StatusRepository = new StatusRepositoryImpl(dirs)

  val changeSetInfoApi = new ChangeSetInfoApiImpl(dirs.changeSets)

  val overpassQueryExecutor = new OverpassQueryExecutorImpl()

  val osmChangeRepository = new OsmChangeRepository(dirs.replicate)

  val analysisData: Watched = Watched()

  val changeSetRepository = new ChangeSetRepositoryImpl(mongoDatabase)

  val orphanRepository = new OrphanRepositoryImpl(mongoDatabase)

  val factRepository = new FactRepositoryImpl(mongoDatabase)

  val routeCountryAnalyzer = new RouteCountryAnalyzer(countryAnalyzer)

  val routeLocationAnalyzer = new RouteLocationAnalyzerImpl(
    routeRepository,
    routeLocator
  )

  val routeAnalyzer = new MasterRouteAnalyzerImpl(
    analysisContext,
    routeCountryAnalyzer,
    routeLocationAnalyzer,
    routeTileAnalyzer
  )

  val networkRelationAnalyzer = new NetworkRelationAnalyzerImpl(countryAnalyzer)

  val replicationId: ReplicationId = ReplicationId(1)
  private val beginOsmChange = osmChangeRepository.get(replicationId)
  val timestamp: Timestamp = TimestampUtil.relativeSeconds(beginOsmChange.timestampUntil.get, -1)

  val changeSetContext: ChangeSetContext = ChangeSetContext(
    replicationId,
    ChangeSet(
      0,
      timestamp,
      timestamp,
      timestamp,
      timestamp,
      timestamp,
      Seq.empty
    ),
    ElementIds()
  )
}
