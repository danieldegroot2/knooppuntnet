package kpn.server.analyzer.engine.changes.orphan.node

import kpn.api.custom.Change
import kpn.server.analyzer.engine.changes.AnalysisTestData
import kpn.server.analyzer.engine.changes.ElementChanges
import kpn.server.repository.MockBlackListRepository
import kpn.api.common.SharedTestObjects
import org.scalatest.FunSuite
import org.scalatest.Matchers

class OrphanNodeChangeAnalyzerTest extends FunSuite with Matchers with SharedTestObjects {

  val d = new AnalysisTestData()

  test("no result for 'Create' of a node that is referenced in network hierarchy") {

    createNode(d.nodeInWatchedNetwork) should equal(ElementChanges())

    createNode(d.nodeInRouteInWatchedNetwork) should equal(ElementChanges())

    createNode(d.nodeInWayInWatchedNetwork) should equal(ElementChanges())
  }

  test("no result for 'Modify' of a node that is referenced in network hierarchy") {

    modifyNode(d.nodeInWatchedNetwork) should equal(ElementChanges())

    modifyNode(d.nodeInRouteInWatchedNetwork) should equal(ElementChanges())

    modifyNode(d.nodeInWayInWatchedNetwork) should equal(ElementChanges())
  }

  test("no result for 'Delete' of a node that is referenced in network hierarchy") {

    deleteNode(d.nodeInWatchedNetwork) should equal(ElementChanges())

    deleteNode(d.nodeInRouteInWatchedNetwork) should equal(ElementChanges())

    deleteNode(d.nodeInWayInWatchedNetwork) should equal(ElementChanges())
  }

  test("no result for 'Create' of a node that is referenced in an orphan route") {

    createNode(d.nodeInWatchedOrphanRoute) should equal(ElementChanges())

    createNode(d.nodeInWayInWatchedOrphanRoute) should equal(ElementChanges())
  }

  test("no result for 'Modify' of a node that is referenced in an orphan route") {

    modifyNode(d.nodeInWatchedOrphanRoute) should equal(ElementChanges())

    modifyNode(d.nodeInWayInWatchedOrphanRoute) should equal(ElementChanges())
  }

  test("no result for 'Delete' of a node that is referenced in an orphan route") {

    deleteNode(d.nodeInWatchedOrphanRoute) should equal(ElementChanges())

    deleteNode(d.nodeInWayInWatchedOrphanRoute) should equal(ElementChanges())
  }

  test("'Create' of a new orphan node") {
    createNode(d.newOrphanNode) should equal(ElementChanges(creates = Seq(d.newOrphanNode)))
  }

  test("'Modify' of a previously unknown orphan node is treated as new orphan node") {
    modifyNode(d.newOrphanNode) should equal(ElementChanges(creates = Seq(d.newOrphanNode)))
  }

  test("'Modify' of an existing orphan node") {
    modifyNode(d.watchedOrphanNode) should equal(ElementChanges(updates = Seq(d.watchedOrphanNode)))
  }

  test("'Delete' of an existing orphan node") {
    deleteNode(d.watchedOrphanNode) should equal(ElementChanges(deletes = Seq(d.watchedOrphanNode)))
  }

  test("'Delete' of unknown orphan node does not have any effect") {
    deleteNode(d.newOrphanNode) should equal(ElementChanges())
  }

  private def createNode(nodeId: Long): ElementChanges = elementChanges(d.createNode(nodeId))

  private def modifyNode(nodeId: Long): ElementChanges = elementChanges(d.modifyNode(nodeId))

  private def deleteNode(nodeId: Long): ElementChanges = elementChanges(d.deleteNode(nodeId))

  private def elementChanges(change: Change): ElementChanges = {
    val cs = newChangeSet(changes = Seq(change))
    val analysis = new OrphanNodeChangeAnalyzerImpl(d.analysisContext, new MockBlackListRepository()).analyze(cs)
    analysis.creates.map(_.id) should equal(analysis.changes.creates)
    analysis.updates.map(_.id) should equal(analysis.changes.updates)
    analysis.deletes.map(_.id) should equal(analysis.changes.deletes)
    analysis.changes
  }
}
