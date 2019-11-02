package kpn.server.analyzer

import kpn.core.database.Database
import kpn.core.database.views.analyzer.AnalyzerDesign
import kpn.core.overpass.OverpassQueryExecutor
import kpn.core.overpass.OverpassQueryExecutorImpl
import kpn.core.tools.config.Dirs
import kpn.core.tools.status.StatusRepository
import kpn.core.tools.status.StatusRepositoryImpl
import kpn.server.analyzer.engine.AnalysisContext
import kpn.server.analyzer.engine.CouchIndexer
import kpn.server.analyzer.engine.changes.OsmChangeRepository
import kpn.server.analyzer.engine.changes.changes.ChangeSetInfoApi
import kpn.server.analyzer.engine.changes.changes.ChangeSetInfoApiImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AnalyzerConfiguration {

  //noinspection VarCouldBeVal
  @Autowired
  var analysisDatabase: Database = _

  @Bean
  def dirs = Dirs()

  @Bean
  def statusRepository: StatusRepository = new StatusRepositoryImpl(dirs)

  @Bean
  def analysisDatabaseIndexer: CouchIndexer = new CouchIndexer(
    analysisDatabase, AnalyzerDesign
  )

  @Bean
  def overpassQueryExecutor: OverpassQueryExecutor = {
    new OverpassQueryExecutorImpl()
  }

  @Bean
  def osmChangeRepository: OsmChangeRepository = {
    new OsmChangeRepository(dirs.replicate)
  }

  @Bean
  def ChangeSetInfoApi: ChangeSetInfoApi = {
    new ChangeSetInfoApiImpl(dirs.changeSets)
  }

  @Bean
  def analysisContext: AnalysisContext = {
    new AnalysisContext()
  }

}
