package kpn.server.analyzer.engine.tiles

import kpn.api.common.NodeName
import kpn.api.common.SharedTestObjects
import kpn.api.custom.Day
import kpn.api.custom.NetworkScope
import kpn.api.custom.NetworkType
import kpn.api.custom.Tags
import kpn.core.doc.NodeDoc
import kpn.core.util.UnitTest
import kpn.server.analyzer.engine.tiles.domain.NodeTileInfo
import kpn.server.analyzer.engine.tiles.domain.TileDataNode

class TileDataNodeBuilderTest extends UnitTest with SharedTestObjects {

  test("rwn_ref") {

    val nodeTileInfo = NodeTileInfo(
      1001L,
      names = Seq(
        NodeName(
          networkType = NetworkType.hiking,
          networkScope = NetworkScope.regional,
          name = "01",
          longName = None,
          proposed = false
        )
      ),
      latitude = "1",
      longitude = "2",
      lastSurvey = None,
      tags = Tags.from("rwn_ref" -> "01"),
      facts = Seq.empty
    )

    tileDataNodeBuilder.build(NetworkType.hiking, nodeTileInfo) should equal(
      Some(
        TileDataNode(
          1001L,
          ref = Some("01"),
          name = None,
          latitude = "1",
          longitude = "2",
          layer = "node",
          surveyDate = None,
          proposed = false
        )
      )
    )
  }

  test("proposed") {

    val nodeTileInfo = NodeTileInfo(
      1001L,
      names = Seq(
        NodeName(
          networkType = NetworkType.hiking,
          networkScope = NetworkScope.regional,
          name = "01",
          longName = None,
          proposed = true
        )
      ),
      latitude = "1",
      longitude = "2",
      lastSurvey = None,
      tags = Tags.from("proposed:rwn_ref" -> "01"),
      facts = Seq.empty
    )

    tileDataNodeBuilder.build(NetworkType.hiking, nodeTileInfo) should equal(
      Some(
        TileDataNode(
          1001L,
          ref = Some("01"),
          name = None,
          latitude = "1",
          longitude = "2",
          layer = "node",
          surveyDate = None,
          proposed = true
        )
      )
    )
  }

  test("rwn_ref = 'o'") {

    val nodeTileInfo = NodeTileInfo(
      1001L,
      names = Seq(
        NodeName(
          networkType = NetworkType.hiking,
          networkScope = NetworkScope.regional,
          name = "o",
          longName = None,
          proposed = false
        )
      ),
      latitude = "",
      longitude = "",
      lastSurvey = None,
      tags = Tags.from("rwn_ref" -> "o"),
      facts = Seq.empty
    )

    tileDataNodeBuilder.build(NetworkType.hiking, nodeTileInfo) should equal(None)
  }

  test("proposed:rwn_ref = 'o'") {

    val nodeTileInfo = NodeTileInfo(
      1001L,
      names = Seq(
        NodeName(
          networkType = NetworkType.hiking,
          networkScope = NetworkScope.regional,
          name = "o",
          longName = None,
          proposed = false
        )
      ),
      latitude = "",
      longitude = "",
      lastSurvey = None,
      tags = Tags.from("proposed:rwn_ref" -> "o"),
      facts = Seq.empty
    )

    tileDataNodeBuilder.build(NetworkType.hiking, nodeTileInfo) should equal(None)
  }

  test("rwn_ref and rwn_name") {

    val nodeTileInfo = NodeTileInfo(
      1001L,
      names = Seq(
        NodeName(
          networkType = NetworkType.hiking,
          networkScope = NetworkScope.regional,
          name = "01",
          longName = Some("name"),
          proposed = false
        )
      ),
      latitude = "1",
      longitude = "2",
      lastSurvey = None,
      tags = Tags.from(
        "rwn_ref" -> "01",
        "rwn_name" -> "name"
      ),
      facts = Seq.empty
    )

    tileDataNodeBuilder.build(NetworkType.hiking, nodeTileInfo) should equal(
      Some(
        TileDataNode(
          1001L,
          ref = Some("01"),
          name = Some("name"),
          latitude = "1",
          longitude = "2",
          layer = "node",
          surveyDate = None,
          proposed = false
        )
      )
    )
  }

  test("rwn_name") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "rwn_name" -> "name"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(None)
    tileDataNode.name should equal(Some("name"))
  }

  test("proposed:rwn_name") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "proposed:rwn_name" -> "name"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(None)
    tileDataNode.name should equal(Some("name"))
  }

  test("rwn:name") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "rwn:name" -> "name"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(None)
    tileDataNode.name should equal(Some("name"))
  }

  test("proposed:rwn:name") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "proposed:rwn:name" -> "name"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(None)
    tileDataNode.name should equal(Some("name"))
  }

  test("rwn_name length less than or equal to 3 characters") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "rwn_name" -> "123"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(Some("123"))
    tileDataNode.name should equal(None)
  }

  test("lwn_ref") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from("lwn_ref" -> "01")
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(Some("01"))
    tileDataNode.name should equal(None)
  }

  test("prefer rwn_ref over lwn_ref") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "lwn_ref" -> "01",
        "rwn_ref" -> "02"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(Some("02"))
    tileDataNode.name should equal(None)
  }

  test("lwn_name") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from("lwn_name" -> "name")
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(None)
    tileDataNode.name should equal(Some("name"))
  }

  test("lwn:name") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from("lwn:name" -> "name")
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.ref should equal(None)
    tileDataNode.name should equal(Some("name"))
  }

  test("survey date") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "rwn_ref" -> "01",
        "survey:date" -> "2020-08-11"
      )
    )

    val tileDataNode = buildTileDataNode(node)
    tileDataNode.surveyDate should equal(Some(Day(2020, 8, 11)))
  }

  test("rwn_ref and rcn_ref") {

    val node = newNodeDoc(
      id = 1001,
      tags = Tags.from(
        "rwn_ref" -> "01",
        "rcn_ref" -> "02"
      )
    )

    pending
    tileDataNodeBuilder.build(NetworkType.hiking, null /*node*/).flatMap(_.ref) should equal(Some("01"))
    tileDataNodeBuilder.build(NetworkType.cycling, null /*node*/).flatMap(_.ref) should equal(Some("02"))
  }

  private def buildTileDataNode(node: NodeDoc): TileDataNode = {
    pending
    tileDataNodeBuilder.build(NetworkType.hiking, null /*node*/).get
  }

  private def tileDataNodeBuilder: TileDataNodeBuilder = {
    new TileDataNodeBuilderImpl()
  }
}
