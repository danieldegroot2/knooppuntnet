package kpn.server.analyzer.engine

import kpn.api.common.ReplicationId

trait AnalyzerEngine {

  def load(replicationId: ReplicationId): Unit

  def process(replicationId: ReplicationId): Unit

}
