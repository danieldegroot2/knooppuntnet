package kpn.server.analyzer.load

import akka.actor.ActorSystem
import kpn.core.app.ActorSystemConfig
import kpn.core.database.DatabaseImpl
import kpn.core.database.implementation.DatabaseContext
import kpn.core.db.couch.Couch
import kpn.core.db.couch.CouchConfig
import kpn.core.overpass.OverpassQueryExecutorImpl
import kpn.core.tools.config.AnalysisRepositoryConfiguration
import kpn.core.util.Log
import kpn.server.analyzer.engine.analysis.ChangeSetInfoUpdaterImpl
import kpn.server.analyzer.engine.analysis.country.CountryAnalyzerImpl
import kpn.server.analyzer.engine.analysis.network.NetworkAnalyzerImpl
import kpn.server.analyzer.engine.analysis.network.NetworkRelationAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.MasterRouteAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.analyzers.AccessibilityAnalyzerImpl
import kpn.server.analyzer.engine.changes.changes.RelationAnalyzerImpl
import kpn.server.analyzer.engine.changes.data.AnalysisData
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.json.Json
import kpn.server.repository.AnalysisRepository
import kpn.server.repository.BlackListRepositoryImpl
import kpn.server.repository.ChangeSetInfoRepositoryImpl
import kpn.server.repository.TaskRepositoryImpl
import kpn.shared.Timestamp

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object NetworksLoaderDemo {

  def main(args: Array[String]): Unit = {
    val system = ActorSystemConfig.actorSystem()
    try {
      new NetworksLoaderDemo(system).run()
    }
    finally {
      Await.result(system.terminate(), Duration.Inf)
    }
  }
}

/**
 * Try out loading of networks with different settings for parallelization.
 */
class NetworksLoaderDemo(system: ActorSystem) {

  val log = Log(classOf[NetworksLoaderDemo])

  val couchConfig: CouchConfig = Couch.config
  val database = new DatabaseImpl(DatabaseContext(couchConfig, Json.objectMapper, "test"))
  val analysisRepository: AnalysisRepository = new AnalysisRepositoryConfiguration(database).analysisRepository
  val executor = new OverpassQueryExecutorImpl()
  val analysisData = AnalysisData()
  val analysisContext = new AnalysisContext()
  val networkIdsLoader = new NetworkIdsLoaderImpl(executor)
  val networkLoader = new NetworkLoaderImpl(executor)
  val relationAnalyzer = new RelationAnalyzerImpl(analysisContext)
  val countryAnalyzer = new CountryAnalyzerImpl(relationAnalyzer)
  val networkRelationAnalyzer = new NetworkRelationAnalyzerImpl(relationAnalyzer, countryAnalyzer)
  val routeAnalyzer = new MasterRouteAnalyzerImpl(analysisContext, new AccessibilityAnalyzerImpl())
  val networkAnalyzer = new NetworkAnalyzerImpl(analysisContext, relationAnalyzer, countryAnalyzer, routeAnalyzer)
  val nodeLoader = new NodeLoaderImpl(executor, executor, countryAnalyzer)
  val changeSetInfoRepository = new ChangeSetInfoRepositoryImpl(database)
  val taskRepository = new TaskRepositoryImpl(database)
  val changeSetInfoUpdater = new ChangeSetInfoUpdaterImpl(changeSetInfoRepository, taskRepository)
  val routeLoader = new RouteLoaderImpl(executor, countryAnalyzer)
  val blackListRepository = new BlackListRepositoryImpl(database)

  private val networkInitialLoaderWorker: NetworkInitialLoaderWorker = new NetworkInitialLoaderWorkerImpl(
    analysisContext,
    analysisRepository,
    networkLoader,
    networkRelationAnalyzer,
    networkAnalyzer,
    blackListRepository
  )

  private val networkInitialLoader: NetworkInitialLoader = new NetworkInitialLoaderImpl(
    system: ActorSystem,
    networkInitialLoaderWorker
  )

  val networksLoader = new NetworksLoaderImpl(
    networkIdsLoader,
    networkInitialLoader
  )

  def run(): Unit = {
    log.unitElapsed {
      networksLoader.load(Timestamp(2016, 9, 1))
      "networksLoader"
    }
  }
}
