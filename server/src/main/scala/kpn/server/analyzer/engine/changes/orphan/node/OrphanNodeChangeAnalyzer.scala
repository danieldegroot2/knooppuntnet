package kpn.server.analyzer.engine.changes.orphan.node

import kpn.api.common.changes.ChangeSet

trait OrphanNodeChangeAnalyzer {
  def analyze(changeSet: ChangeSet): OrphanNodeChangeAnalysis
}
