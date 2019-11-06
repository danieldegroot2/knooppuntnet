package kpn.server.analyzer.engine.changes.integration

import java.io.PrintWriter
import java.io.StringWriter

import kpn.server.analyzer.engine.changes.changes.RelationAnalyzer
import kpn.server.analyzer.engine.changes.changes.RelationAnalyzerImpl
import kpn.core.data.Data
import kpn.server.analyzer.engine.analysis.ChangeSetInfoUpdaterImpl
import kpn.server.analyzer.engine.analysis.country.CountryAnalyzer
import kpn.server.analyzer.engine.analysis.country.CountryAnalyzerMock
import kpn.server.analyzer.engine.analysis.route.MasterRouteAnalyzerImpl
import kpn.server.analyzer.engine.analysis.route.analyzers.AccessibilityAnalyzerImpl
import kpn.server.analyzer.engine.changes.ChangeProcessor
import kpn.server.analyzer.engine.changes.ChangeSaverImpl
import kpn.server.analyzer.engine.changes.ChangeSetContext
import kpn.server.analyzer.engine.changes.builder.ChangeBuilder
import kpn.server.analyzer.engine.changes.builder.ChangeBuilderImpl
import kpn.server.analyzer.engine.changes.builder.NodeChangeBuilder
import kpn.server.analyzer.engine.changes.builder.NodeChangeBuilderImpl
import kpn.server.analyzer.engine.changes.builder.RouteChangeBuilder
import kpn.server.analyzer.engine.changes.builder.RouteChangeBuilderImpl
import kpn.server.analyzer.engine.changes.data.AnalysisData
import kpn.server.analyzer.engine.changes.data.BlackList
import kpn.server.analyzer.engine.changes.network.NetworkChangeAnalyzerImpl
import kpn.server.analyzer.engine.changes.network.NetworkChangeProcessorImpl
import kpn.server.analyzer.engine.changes.network.create.NetworkCreateProcessor
import kpn.server.analyzer.engine.changes.network.create.NetworkCreateProcessorSyncImpl
import kpn.server.analyzer.engine.changes.network.create.NetworkCreateProcessorWorkerImpl
import kpn.server.analyzer.engine.changes.network.create.NetworkCreateWatchedProcessorImpl
import kpn.server.analyzer.engine.changes.network.delete.NetworkDeleteProcessorSyncImpl
import kpn.server.analyzer.engine.changes.network.delete.NetworkDeleteProcessorWorkerImpl
import kpn.server.analyzer.engine.changes.network.update.NetworkUpdateNetworkProcessorImpl
import kpn.server.analyzer.engine.changes.network.update.NetworkUpdateProcessor
import kpn.server.analyzer.engine.changes.network.update.NetworkUpdateProcessorSyncImpl
import kpn.server.analyzer.engine.changes.network.update.NetworkUpdateProcessorWorkerImpl
import kpn.server.analyzer.engine.changes.orphan.node.OrphanNodeChangeAnalyzerImpl
import kpn.server.analyzer.engine.changes.orphan.node.OrphanNodeChangeProcessorImpl
import kpn.server.analyzer.engine.changes.orphan.node.OrphanNodeCreateProcessorImpl
import kpn.server.analyzer.engine.changes.orphan.node.OrphanNodeDeleteProcessorImpl
import kpn.server.analyzer.engine.changes.orphan.node.OrphanNodeUpdateProcessorImpl
import kpn.server.analyzer.engine.changes.orphan.route.OrphanRouteChangeAnalyzer
import kpn.server.analyzer.engine.changes.orphan.route.OrphanRouteChangeProcessorImpl
import kpn.server.analyzer.engine.changes.orphan.route.OrphanRouteProcessor
import kpn.server.analyzer.engine.changes.orphan.route.OrphanRouteProcessorImpl
import kpn.server.analyzer.load.NetworkLoader
import kpn.server.analyzer.load.NetworkLoaderImpl
import kpn.server.analyzer.load.NodeLoaderImpl
import kpn.server.analyzer.load.RouteLoaderImpl
import kpn.server.analyzer.load.RoutesLoaderSyncImpl
import kpn.server.analyzer.load.data.RawDataSplitter
import kpn.core.loadOld.OsmDataXmlWriter
import kpn.core.overpass.OverpassQuery
import kpn.core.overpass.OverpassQueryExecutor
import kpn.core.overpass.QueryNode
import kpn.core.overpass.QueryNodes
import kpn.core.overpass.QueryRelation
import kpn.server.analyzer.engine.analysis.network.NetworkAnalyzerImpl
import kpn.server.analyzer.engine.analysis.network.NetworkRelationAnalyzerImpl
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.repository.AnalysisRepository
import kpn.server.repository.BlackListRepository
import kpn.server.repository.ChangeSetInfoRepository
import kpn.server.repository.ChangeSetRepository
import kpn.server.repository.NetworkRepository
import kpn.server.repository.NodeRepository
import kpn.server.repository.TaskRepository
import kpn.shared.ReplicationId
import kpn.shared.SharedTestObjects
import kpn.shared.Timestamp
import kpn.shared.changes.Change
import kpn.shared.data.raw.RawData
import kpn.shared.data.raw.RawElement
import kpn.shared.data.raw.RawNode
import kpn.shared.data.raw.RawRelation
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.Matchers

abstract class AbstractTest extends FunSuite with Matchers with MockFactory with SharedTestObjects {

  protected def node(data: Data, id: Long): RawNode = {
    data.raw.nodeWithId(id).get
  }

  protected def relation(data: Data, id: Long): RawRelation = {
    data.raw.relationWithId(id).get
  }

  protected class TestConfig() {

    val analysisContext = new AnalysisContext()

    val relationAnalyzer: RelationAnalyzer = new RelationAnalyzerImpl(analysisContext)

    val countryAnalyzer: CountryAnalyzer = new CountryAnalyzerMock(relationAnalyzer)
    val executor: OverpassQueryExecutor = stub[OverpassQueryExecutor]

    val analysisRepository: AnalysisRepository = stub[AnalysisRepository]
    val networkRepository: NetworkRepository = stub[NetworkRepository]
    val changeSetRepository: ChangeSetRepository = stub[ChangeSetRepository]
    val nodeRepository: NodeRepository = stub[NodeRepository]
    val changeSetInfoRepository: ChangeSetInfoRepository = stub[ChangeSetInfoRepository]
    private val taskRepository: TaskRepository = stub[TaskRepository]

    private val blackListRepository: BlackListRepository = stub[BlackListRepository]
    (blackListRepository.get _).when().returns(BlackList())

    private val nodeLoader = new NodeLoaderImpl(executor, executor, countryAnalyzer)
    private val routeLoader = new RouteLoaderImpl(executor, countryAnalyzer)
    private val networkLoader: NetworkLoader = new NetworkLoaderImpl(executor)
    private val routeAnalyzer = new MasterRouteAnalyzerImpl(analysisContext, new AccessibilityAnalyzerImpl())
    private val networkRelationAnalyzer = new NetworkRelationAnalyzerImpl(relationAnalyzer, countryAnalyzer)
    private val networkAnalyzer = new NetworkAnalyzerImpl(analysisContext, relationAnalyzer, countryAnalyzer, routeAnalyzer)

    private val nodeChangeBuilder: NodeChangeBuilder = new NodeChangeBuilderImpl(
      analysisContext,
      analysisRepository,
      nodeLoader
    )

    private val routeChangeBuilder: RouteChangeBuilder = new RouteChangeBuilderImpl(
      analysisContext,
      analysisRepository,
      relationAnalyzer,
      countryAnalyzer,
      routeAnalyzer,
      routeLoader
    )

    private val routesLoader = new RoutesLoaderSyncImpl(
      routeLoader
    )

    val changeBuilder: ChangeBuilder = new ChangeBuilderImpl(
      analysisContext,
      routesLoader,
      countryAnalyzer,
      routeAnalyzer,
      routeChangeBuilder,
      nodeChangeBuilder
    )

    private val networkChangeProcessor = {

      val networkChangeAnalyzer = new NetworkChangeAnalyzerImpl(
        analysisContext,
        blackListRepository
      )

      val networkCreateProcessor: NetworkCreateProcessor = {

        val watchedProcessor = new NetworkCreateWatchedProcessorImpl(
          analysisContext,
          analysisRepository,
          networkRelationAnalyzer,
          networkAnalyzer,
          changeBuilder
        )

        val worker = new NetworkCreateProcessorWorkerImpl(
          networkLoader,
          networkRelationAnalyzer,
          watchedProcessor
        )

        new NetworkCreateProcessorSyncImpl(
          worker
        )
      }

      val networkUpdateProcessor: NetworkUpdateProcessor = {

        val networkUpdateNetworkProcessor = new NetworkUpdateNetworkProcessorImpl(
          analysisContext,
          analysisRepository,
          networkRelationAnalyzer,
          networkAnalyzer,
          changeBuilder
        )

        val worker = new NetworkUpdateProcessorWorkerImpl(
          analysisRepository,
          networkLoader,
          networkRelationAnalyzer,
          networkUpdateNetworkProcessor
        )

        new NetworkUpdateProcessorSyncImpl(
          worker
        )
      }

      val networkDeleteProcessor = {

        val worker = new NetworkDeleteProcessorWorkerImpl(
          analysisContext,
          networkRepository,
          networkLoader,
          networkRelationAnalyzer,
          networkAnalyzer,
          changeBuilder
        )

        new NetworkDeleteProcessorSyncImpl(
          worker
        )
      }

      new NetworkChangeProcessorImpl(
        networkChangeAnalyzer,
        networkCreateProcessor,
        networkUpdateProcessor,
        networkDeleteProcessor
      )
    }

    private val changeSetInfoUpdater = new ChangeSetInfoUpdaterImpl(
      changeSetInfoRepository,
      taskRepository
    )

    private val orphanRouteChangeProcessor = {
      val orphanRouteProcessor: OrphanRouteProcessor = new OrphanRouteProcessorImpl(
        analysisContext,
        analysisRepository,
        relationAnalyzer,
        countryAnalyzer,
        routeAnalyzer
      )
      val orphanRouteChangeAnalyzer = new OrphanRouteChangeAnalyzer(
        analysisContext,
        blackListRepository
      )
      new OrphanRouteChangeProcessorImpl(
        analysisContext,
        analysisRepository,
        orphanRouteChangeAnalyzer,
        orphanRouteProcessor,
        routesLoader,
        routeAnalyzer,
        countryAnalyzer
      )
    }

    private val orphanNodeChangeProcessor = {

      val orphanNodeChangeAnalyzer = new OrphanNodeChangeAnalyzerImpl(
        analysisContext,
        blackListRepository
      )

      val orphanNodeDeleteProcessor = new OrphanNodeDeleteProcessorImpl(
        analysisContext,
        analysisRepository,
        countryAnalyzer
      )

      val orphanNodeCreateProcessor = new OrphanNodeCreateProcessorImpl(
        analysisContext,
        analysisRepository
      )

      val orphanNodeUpdateProcessor = new OrphanNodeUpdateProcessorImpl(
        analysisContext,
        analysisRepository
      )

      new OrphanNodeChangeProcessorImpl(
        orphanNodeChangeAnalyzer,
        orphanNodeCreateProcessor,
        orphanNodeUpdateProcessor,
        orphanNodeDeleteProcessor,
        countryAnalyzer,
        nodeLoader
      )
    }

    val changeProcessor: ChangeProcessor = {
      val changeSaver = new ChangeSaverImpl(
        changeSetRepository
      )
      new ChangeProcessor(
        changeSetRepository,
        networkChangeProcessor,
        orphanRouteChangeProcessor,
        orphanNodeChangeProcessor,
        changeSetInfoUpdater,
        changeSaver
      )
    }

    def process(action: Int, element: RawElement): Unit = {
      val changes = Seq(Change(action, Seq(element)))
      process(changes)
    }

    def process(changes: Seq[Change]): Unit = {
      val changeSet = newChangeSet(changes = changes)
      val changeSetContext = ChangeSetContext(ReplicationId(1), changeSet)
      changeProcessor.process(changeSetContext)
    }

    def relationBefore(data: Data, relationId: Long): Unit = {
      overpassQueryRelation(data, timestampBeforeValue, relationId)
    }

    def relationAfter(data: Data, relationId: Long): Unit = {
      overpassQueryRelation(data, timestampAfterValue, relationId)
    }

    def nodeBefore(data: Data, nodeId: Long): Unit = {
      overpassQueryNode(data, timestampBeforeValue, nodeId)
    }

    def nodeAfter(data: Data, nodeId: Long): Unit = {
      overpassQueryNode(data, timestampAfterValue, nodeId)
    }

    def nodesBefore(data: Data, nodeIds: Long*): Unit = {
      overpassQueryNodes(data, timestampBeforeValue, nodeIds)
    }

    def nodesAfter(data: Data, nodeIds: Long*): Unit = {
      overpassQueryNodes(data, timestampAfterValue, nodeIds)
    }

    def watchNetwork(data: Data, networkId: Long): Unit = {
      analysisContext.data.networks.watched.add(networkId, relationAnalyzer.toElementIds(data.relations(networkId)))
    }

    def watchOrphanRoute(data: Data, routeId: Long): Unit = {
      val elementIds = relationAnalyzer.toElementIds(data.relations(routeId))
      analysisContext.data.orphanRoutes.watched.add(routeId, elementIds)
    }

    def watchOrphanNode(nodeId: Long): Unit = {
      analysisContext.data.orphanNodes.watched.add(nodeId)
    }

    private def overpassQueryRelation(data: Data, timestamp: Timestamp, relationId: Long): Unit = {
      val xml = toXml(RawDataSplitter.extractRelation(data.raw, relationId))
      overpassQuery(timestamp, QueryRelation(relationId), xml)
    }

    private def overpassQueryNode(data: Data, timestamp: Timestamp, nodeId: Long): Unit = {
      val xml = toXml(RawData(Some(timestamp), data.raw.nodeWithId(nodeId).toSeq))
      overpassQuery(timestamp, QueryNode(nodeId), xml)
    }

    private def overpassQueryNodes(data: Data, timestamp: Timestamp, nodeIds: Seq[Long]): Unit = {
      val nodes = nodeIds.flatMap(nodeId => data.raw.nodeWithId(nodeId))
      val xml = toXml(RawData(Some(timestamp), nodes))
      overpassQuery(timestamp, QueryNodes("nodes", nodeIds), xml)
    }

    private def overpassQuery(timestamp: Timestamp, query: OverpassQuery, xml: String): Unit = {
      (executor.executeQuery _).when(Some(timestamp), query).returns(xml).anyNumberOfTimes()
      ()
    }

    private def toXml(data: RawData): String = {
      val sw = new StringWriter()
      val pw = new PrintWriter(sw)
      new OsmDataXmlWriter(data, pw).print()
      sw.toString
    }
  }

}
