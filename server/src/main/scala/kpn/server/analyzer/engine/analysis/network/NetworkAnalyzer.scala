package kpn.server.analyzer.engine.analysis.network

import kpn.core.analysis.Network
import kpn.server.analyzer.load.data.LoadedNetwork

trait NetworkAnalyzer {
  def analyze(networkRelationAnalysis: NetworkRelationAnalysis, loadedNetwork: LoadedNetwork): Network
}
