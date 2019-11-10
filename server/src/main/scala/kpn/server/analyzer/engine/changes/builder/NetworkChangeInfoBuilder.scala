package kpn.server.analyzer.engine.changes.builder

import kpn.api.common.changes.ChangeSetInfo
import kpn.api.common.changes.details.NetworkChange
import kpn.api.common.changes.details.NetworkChangeInfo

class NetworkChangeInfoBuilder {

  def build(change: NetworkChange, changeSetInfos: Seq[ChangeSetInfo]): NetworkChangeInfo = {

    val comment = changeSetInfos.find(s => s.id == change.key.changeSetId).flatMap { changeSetInfo =>
      changeSetInfo.tags("comment")
    }

    NetworkChangeInfo(
      comment,
      change.key,
      change.changeType,
      change.country,
      change.networkType,
      change.networkId,
      change.networkName,
      change.networkDataUpdate.map(_.before.relation.toMeta),
      change.networkDataUpdate.map(_.after.relation.toMeta),
      change.orphanRoutes,
      change.orphanNodes,
      change.networkDataUpdate.isDefined,
      change.networkNodes,
      change.routes,
      change.nodes,
      change.ways,
      change.relations,
      change.happy,
      change.investigate
    )
  }
}
