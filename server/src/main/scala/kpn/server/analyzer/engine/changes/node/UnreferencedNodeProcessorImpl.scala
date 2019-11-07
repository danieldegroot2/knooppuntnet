package kpn.server.analyzer.engine.changes.node

import kpn.core.analysis.NetworkNodeInfo
import kpn.core.history.NodeMovedAnalyzer
import kpn.core.history.NodeTagDiffAnalyzer
import kpn.server.analyzer.engine.analysis.country.CountryAnalyzer
import kpn.server.analyzer.engine.analysis.node.NodeAnalyzer
import kpn.server.analyzer.engine.changes.ChangeSetContext
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.analyzer.load.NodeLoader
import kpn.server.analyzer.load.data.LoadedNode
import kpn.server.repository.AnalysisRepository
import kpn.server.repository.NodeInfoBuilder.fromLoadedNode
import kpn.server.repository.NodeInfoBuilder.fromNetworkNodeInfo
import kpn.shared.Fact
import kpn.shared.Subset
import kpn.shared.changes.details.ChangeType
import kpn.shared.changes.details.NodeChange
import kpn.shared.diff.common.FactDiffs
import org.springframework.stereotype.Component

/*
  Processes nodes that potentially are not referenced anymore, for example by a network for which
  the network relation is deleted, or a nodes that are no longer part of a network after a network update.
 */
@Component
class UnreferencedNodeProcessorImpl(
  analysisContext: AnalysisContext,
  analysisRepository: AnalysisRepository,
  nodeLoader: NodeLoader,
  countryAnalyzer: CountryAnalyzer
) extends UnreferencedNodeProcessor {

  override def process(context: ChangeSetContext, candidateUnreferencedNodes: Seq[NetworkNodeInfo]): Seq[NodeChange] = {
    val unreferencedNodes = candidateUnreferencedNodes.filterNot(isReferencedNode)
    val nodesAfter = nodeLoader.loadNodes(context.timestampAfter, unreferencedNodes.map(_.id))
    unreferencedNodes.flatMap { nodeBefore =>
      val loadedNodeAfter = nodesAfter.find(_.id == nodeBefore.id)
      process1(context, nodeBefore, loadedNodeAfter)
    }
  }

  private def process1(
    context: ChangeSetContext,
    nodeBefore: NetworkNodeInfo,
    loadedNodeAfter: Option[LoadedNode]
  ): Option[NodeChange] = {

    loadedNodeAfter match {
      case None => processDeletedNode(context, nodeBefore)
      case Some(nodeAfter) =>
        nodeAfter.country match {
          case None =>
            // cannot determine country: do not process any further
            // TODO CHANGE remove from orphan/ignored node lists? + add in test
            // BecomeIgnored ???
            None

          case Some(country) =>
            process(context, nodeBefore, nodeAfter)
        }
    }
  }

  private def process(context: ChangeSetContext, nodeBefore: NetworkNodeInfo, nodeAfter: LoadedNode) = {

    val before = nodeBefore.networkNode.node.raw
    val after = nodeAfter.node.raw

    val facts = {
      val ff = new NodeChangeFactAnalyzer(analysisContext.data).facts(before, after)
      if (ff.contains(Fact.LostHikingNodeTag) || ff.contains(Fact.LostHikingNodeTag)) {
        ff
      }
      else {
        ff :+ Fact.BecomeOrphan
      }
    }

    if (facts.isEmpty || NodeAnalyzer.hasNodeTag(after.tags)) {
      furtherProcess(context, nodeBefore, nodeAfter, facts)
    }
    else {
      val nodeInfo = fromLoadedNode(nodeAfter, active = false)
      analysisRepository.saveNode(nodeInfo)

      val subsets = NodeAnalyzer.networkTypes(nodeBefore.networkNode.tags).flatMap { networkType =>
        nodeBefore.networkNode.country.flatMap(country => Subset.of(country, networkType))
      }

      val tagDiffs = new NodeTagDiffAnalyzer(before, after).diffs
      val nodeMoved = new NodeMovedAnalyzer(before, after).analysis

      analysisContext.data.orphanNodes.watched.delete(before.id)

      Some(
        analyzed(
          NodeChange(
            key = context.buildChangeKey(nodeBefore.id),
            changeType = ChangeType.Update,
            subsets = subsets,
            name = nodeBefore.networkNode.name,
            before = Some(nodeBefore.networkNode.node.raw),
            after = Some(nodeAfter.node.raw),
            connectionChanges = Seq.empty,
            roleConnectionChanges = Seq.empty,
            definedInNetworkChanges = Seq.empty,
            tagDiffs = tagDiffs,
            nodeMoved = nodeMoved,
            addedToRoute = Seq.empty,
            removedFromRoute = Seq.empty,
            addedToNetwork = Seq.empty,
            removedFromNetwork = Seq.empty,
            factDiffs = FactDiffs(),
            facts = facts
          )
        )
      )
    }
  }

  private def processDeletedNode(context: ChangeSetContext, nodeBefore: NetworkNodeInfo) = {

    val nodeInfo = fromNetworkNodeInfo(nodeBefore, active = false, facts = Seq(Fact.Deleted))
    analysisRepository.saveNode(nodeInfo)

    val subsets = NodeAnalyzer.networkTypes(nodeBefore.networkNode.tags).flatMap { networkType =>
      nodeBefore.networkNode.country.flatMap(country => Subset.of(country, networkType))
    }

    // TODO CHANGE remove from orphan/ignored node lists? + add in test

    Some(
      analyzed(
        NodeChange(
          key = context.buildChangeKey(nodeBefore.id),
          changeType = ChangeType.Delete,
          subsets = subsets,
          name = nodeBefore.networkNode.name,
          before = Some(nodeBefore.networkNode.node.raw),
          after = None,
          connectionChanges = Seq.empty,
          roleConnectionChanges = Seq.empty,
          definedInNetworkChanges = Seq.empty,
          tagDiffs = None,
          nodeMoved = None,
          addedToRoute = Seq.empty,
          removedFromRoute = Seq.empty,
          addedToNetwork = Seq.empty,
          removedFromNetwork = Seq.empty,
          factDiffs = FactDiffs(),
          facts = Seq(Fact.Deleted)
        )
      )
    )
  }

  private def furtherProcess(context: ChangeSetContext, nodeBefore: NetworkNodeInfo, nodeAfter: LoadedNode, changeFacts: Seq[Fact]): Option[NodeChange] = {

    analysisContext.data.orphanNodes.watched.add(nodeBefore.id)
    val nodeInfo = fromLoadedNode(nodeAfter, orphan = true)
    analysisRepository.saveNode(nodeInfo)

    val rawNodeBefore = nodeBefore.networkNode.node.raw
    val rawNodeAfter = nodeAfter.node.raw

    val tagDiffs = new NodeTagDiffAnalyzer(rawNodeBefore, rawNodeAfter).diffs
    val nodeMoved = new NodeMovedAnalyzer(rawNodeBefore, rawNodeAfter).analysis

    Some(
      analyzed(
        NodeChange(
          key = context.buildChangeKey(nodeBefore.id),
          changeType = ChangeType.Update,
          subsets = nodeAfter.subsets,
          name = nodeAfter.name,
          before = Some(rawNodeBefore),
          after = Some(rawNodeAfter),
          connectionChanges = Seq.empty, // TODO CHANGE supply value here
          roleConnectionChanges = Seq.empty, // TODO CHANGE supply value here
          definedInNetworkChanges = Seq.empty, // TODO CHANGE supply value here
          tagDiffs = tagDiffs,
          nodeMoved = nodeMoved,
          addedToRoute = Seq.empty,
          removedFromRoute = Seq.empty,
          addedToNetwork = Seq.empty,
          removedFromNetwork = Seq.empty,
          factDiffs = FactDiffs(),
          facts = changeFacts :+ Fact.BecomeOrphan
        )
      )
    )
  }

  private def isReferencedNode(node: NetworkNodeInfo): Boolean = {
    analysisContext.data.networks.isReferencingNode(node.id) ||
      analysisContext.data.orphanRoutes.watched.isReferencingNode(node.id)
  }

  private def analyzed(nodeChange: NodeChange): NodeChange = {
    new NodeChangeAnalyzer(nodeChange).analyzed()
  }

}
