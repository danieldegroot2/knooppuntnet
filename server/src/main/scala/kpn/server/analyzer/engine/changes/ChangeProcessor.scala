package kpn.server.analyzer.engine.changes

import kpn.server.analyzer.engine.analysis.ChangeSetInfoUpdater
import kpn.server.analyzer.engine.changes.network.NetworkChangeProcessor
import kpn.server.analyzer.engine.changes.network.info.NetworkInfoChangeProcessor
import kpn.server.analyzer.engine.changes.node.NodeChangeProcessor
import kpn.server.analyzer.engine.changes.route.RouteChangeProcessor
import org.springframework.stereotype.Component

@Component
class ChangeProcessor(
  networkChangeProcessor: NetworkChangeProcessor,
  routeChangeProcessor: RouteChangeProcessor,
  nodeChangeProcessor: NodeChangeProcessor,
  networkInfoChangeProcessor: NetworkInfoChangeProcessor,
  changeSetInfoUpdater: ChangeSetInfoUpdater,
  changeSaver: ChangeSaver
) {

  def process(context: ChangeSetContext): ChangeSetContext = {

    val context1 = networkChangeProcessor.process(context)
    val context2 = routeChangeProcessor.process(context1)
    val context3 = nodeChangeProcessor.process(context2)
    val context4 = networkInfoChangeProcessor.analyze(context3)

    if (context4.changes.nonEmpty) {
      changeSetInfoUpdater.changeSetInfo(context4.changeSet.id)
      changeSaver.save(context4)
    }
    context4
  }
}
