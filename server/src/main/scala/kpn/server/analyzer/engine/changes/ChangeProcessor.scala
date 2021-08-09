package kpn.server.analyzer.engine.changes

import kpn.server.analyzer.engine.analysis.ChangeSetInfoUpdater
import kpn.server.analyzer.engine.changes.data.ChangeSetChangesMerger.merge
import kpn.server.analyzer.engine.changes.network.NetworkChangeProcessor
import kpn.server.analyzer.engine.changes.orphan.node.NodeChangeProcessor
import kpn.server.analyzer.engine.changes.orphan.route.OrphanRouteChangeProcessor
import kpn.server.analyzer.engine.changes.route.RouteChangeProcessor
import org.springframework.stereotype.Component

/*
  Process all changes in the change set.
 */
@Component
class ChangeProcessor(
  routeChangeProcessor: RouteChangeProcessor,
  networkChangeProcessor: NetworkChangeProcessor,
  orphanRouteChangeProcessor: OrphanRouteChangeProcessor,
  orphanNodeChangeProcessor: NodeChangeProcessor,
  changeSetInfoUpdater: ChangeSetInfoUpdater,
  changeSaver: ChangeSaver
) {

  def process(context: ChangeSetContext): Unit = {

    val routeChangeSetChanges = routeChangeProcessor.process(context)

    //    val networkChangeSetChanges = networkChangeProcessor.process(context)
    //    val orphanRouteChangeSetChanges = orphanRouteChangeProcessor.process(context)
    val orphanNodeChangeSetChanges = orphanNodeChangeProcessor.process(context)

    val changeSetChanges = merge(
      routeChangeSetChanges,
      // networkChangeSetChanges,
      // orphanRouteChangeSetChanges,
      // orphanNodeChangeSetChanges
    )

    if (changeSetChanges.nonEmpty) {
      changeSetInfoUpdater.changeSetInfo(context.changeSet.id)
      changeSaver.save(context.replicationId, context.changeSet, changeSetChanges)
    }
  }
}
