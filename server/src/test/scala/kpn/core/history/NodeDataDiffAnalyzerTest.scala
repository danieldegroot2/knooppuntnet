package kpn.core.history

import kpn.api.common.LatLonImpl
import kpn.api.common.SharedTestObjects
import kpn.api.common.diff.NodeData
import kpn.api.common.diff.NodeDataUpdate
import kpn.api.common.diff.TagDetail
import kpn.api.common.diff.TagDetailType
import kpn.api.common.diff.TagDiffs
import kpn.api.common.diff.node.NodeMoved
import kpn.api.custom.Tags
import kpn.api.custom.Timestamp
import kpn.core.util.UnitTest

class NodeDataDiffAnalyzerTest extends UnitTest with SharedTestObjects {

  test("no change") {
    val n1 = nodeData(1, "51.5291500", "4.297700", 3, Timestamp(2015, 8, 11, 0, 0, 0), 100, Tags.from("a" -> "1"))
    val n2 = nodeData(1, "51.5291500", "4.297700", 3, Timestamp(2015, 8, 11, 0, 0, 0), 100, Tags.from("a" -> "1"))
    new NodeDataDiffAnalyzer(n1, n2).analysis should equal(None)
  }

  test("node moved") {
    val n1 = nodeData(1, "51.5291500", "4.297700", 3, Timestamp(2015, 8, 11, 0, 0, 0), 100, Tags.from("a" -> "1"))
    val n2 = nodeData(1, "51.5291600", "4.297800", 4, Timestamp(2015, 8, 11, 12, 0, 0), 100, Tags.from("a" -> "1"))
    new NodeDataDiffAnalyzer(n1, n2).analysis.value.shouldMatchTo(
      NodeDataUpdate(
        n1,
        n2,
        None,
        Some(NodeMoved(LatLonImpl("51.5291500", "4.297700"),
          LatLonImpl("51.5291600", "4.297800"), 7)
        )
      )
    )
  }

  test("tags changed") {
    val n1 = nodeData(1, "51.5291500", "4.297700", 3, Timestamp(2015, 8, 11, 0, 0, 0), 100, Tags.from("a" -> "1"))
    val n2 = nodeData(1, "51.5291500", "4.297700", 4, Timestamp(2015, 8, 11, 12, 0, 0), 100, Tags.from("a" -> "2"))
    val expectedDiffs = TagDiffs(Seq.empty, Seq(TagDetail(TagDetailType.Update, "a", Some("1"), Some("2"))))
    new NodeDataDiffAnalyzer(n1, n2).analysis.value.shouldMatchTo(NodeDataUpdate(n1, n2, Some(expectedDiffs)))
  }

  test("only meta info changed") {
    val n1 = nodeData(1, "51.5291500", "4.297700", 3, Timestamp(2015, 8, 11, 0, 0, 0), 100, Tags.from("a" -> "1"))
    val n2 = nodeData(1, "51.5291500", "4.297700", 4, Timestamp(2015, 8, 11, 12, 0, 0), 101, Tags.from("a" -> "1"))
    new NodeDataDiffAnalyzer(n1, n2).analysis.value.shouldMatchTo(NodeDataUpdate(n1, n2, None))
  }

  def nodeData(
    id: Long,
    latitude: String,
    longitude: String,
    version: Int = 0,
    timestamp: Timestamp,
    changeSetId: Long,
    tags: Tags
  ): NodeData = {
    val rawNode = newNode(id, latitude, longitude, version, timestamp, changeSetId, tags).raw
    NodeData(
      Seq.empty,
      "name",
      rawNode
    )
  }

}
