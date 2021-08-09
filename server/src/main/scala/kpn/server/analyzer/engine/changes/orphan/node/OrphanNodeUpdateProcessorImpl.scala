package kpn.server.analyzer.engine.changes.orphan.node

import kpn.api.common.changes.details.ChangeType
import kpn.api.common.changes.details.NodeChange
import kpn.api.common.diff.NodeData
import kpn.api.common.diff.common.FactDiffs
import kpn.api.custom.Fact
import kpn.api.custom.NetworkType
import kpn.core.analysis.TagInterpreter
import kpn.core.history.NodeDataDiffAnalyzer
import kpn.server.analyzer.engine.analysis.node.NodeAnalyzer
import kpn.server.analyzer.engine.analysis.node.domain.NodeAnalysis
import kpn.server.analyzer.engine.changes.ChangeSetContext
import kpn.server.analyzer.engine.changes.node.NodeChangeAnalyzer
import kpn.server.analyzer.engine.context.AnalysisContext
import kpn.server.repository.NodeRepository
import org.springframework.stereotype.Component

@Component
class OrphanNodeUpdateProcessorImpl(
  analysisContext: AnalysisContext,
  nodeRepository: NodeRepository,
  nodeAnalyzer: NodeAnalyzer
) extends OrphanNodeUpdateProcessor {

  override def process(context: ChangeSetContext, loadedNodeChange: LoadedNodeChange): Option[NodeChange] = {

    val facts = {
      val lostNodeTagFacts = Seq(
        lostNodeTag(NetworkType.hiking, loadedNodeChange, Fact.LostHikingNodeTag),
        lostNodeTag(NetworkType.cycling, loadedNodeChange, Fact.LostBicycleNodeTag),
        lostNodeTag(NetworkType.horseRiding, loadedNodeChange, Fact.LostHorseNodeTag),
        lostNodeTag(NetworkType.motorboat, loadedNodeChange, Fact.LostMotorboatNodeTag),
        lostNodeTag(NetworkType.canoe, loadedNodeChange, Fact.LostCanoeNodeTag),
        lostNodeTag(NetworkType.inlineSkating, loadedNodeChange, Fact.LostInlineSkateNodeTag)
      ).flatten

      if (lostNodeTagFacts.nonEmpty) {
        Seq(Fact.WasOrphan) ++ lostNodeTagFacts
      }
      else {
        Seq(Fact.OrphanNode)
      }
    }

    val isNetworkNodeX = TagInterpreter.isValidNetworkNode(loadedNodeChange.after.node.raw)

    if (!isNetworkNodeX) {
      analysisContext.data.nodes.watched.delete(loadedNodeChange.id)
    }

    val before = NodeData(
      loadedNodeChange.before.subsets,
      loadedNodeChange.before.name,
      loadedNodeChange.before.node.raw
    )

    val after = NodeData(
      loadedNodeChange.after.subsets,
      loadedNodeChange.after.name,
      loadedNodeChange.after.node.raw
    )

    val nodeDataUpdate = new NodeDataDiffAnalyzer(before, after).analysis

    val nodeAfterAnalysis = nodeAnalyzer.analyze(
      NodeAnalysis(
        loadedNodeChange.after.node.raw,
        active = isNetworkNodeX,
        orphan = true
      )
    )

    nodeRepository.save(nodeAfterAnalysis.toNodeDoc)

    val subsets = (loadedNodeChange.before.subsets.toSet ++ loadedNodeChange.after.subsets.toSet).toSeq
    val name = if (loadedNodeChange.after.name.nonEmpty) {
      loadedNodeChange.after.name
    }
    else {
      loadedNodeChange.before.name
    }

    val key = context.buildChangeKey(loadedNodeChange.after.node.id)

    Some(
      analyzed(
        NodeChange(
          _id = key.toId,
          key = key,
          changeType = ChangeType.Update,
          subsets = subsets,
          location = nodeAfterAnalysis.oldLocation,
          name = name,
          before = Some(loadedNodeChange.before.node.raw),
          after = Some(loadedNodeChange.after.node.raw),
          connectionChanges = Seq.empty,
          roleConnectionChanges = Seq.empty,
          definedInNetworkChanges = Seq.empty,
          tagDiffs = nodeDataUpdate.flatMap(_.tagDiffs),
          nodeMoved = nodeDataUpdate.flatMap(_.nodeMoved),
          addedToRoute = Seq.empty,
          removedFromRoute = Seq.empty,
          addedToNetwork = Seq.empty,
          removedFromNetwork = Seq.empty,
          factDiffs = FactDiffs(),
          facts
        )
      )
    )
  }

  private def analyzed(nodeChange: NodeChange): NodeChange = {
    new NodeChangeAnalyzer(nodeChange).analyzed()
  }

  private def lostNodeTag(networkType: NetworkType, loadedNodeChange: LoadedNodeChange, fact: Fact): Option[Fact] = {
    if (TagInterpreter.isValidNetworkNode(networkType, loadedNodeChange.before.node.raw) &&
      !TagInterpreter.isValidNetworkNode(networkType, loadedNodeChange.after.node.raw)) {
      Some(fact)
    }
    else {
      None
    }
  }
}
