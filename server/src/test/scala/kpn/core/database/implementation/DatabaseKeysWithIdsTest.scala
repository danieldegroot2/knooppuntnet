package kpn.core.database.implementation

import kpn.core.TestObjects
import kpn.core.database.doc.NodeDoc
import kpn.core.test.TestSupport.withCouchDatabase
import kpn.core.util.UnitTest

class DatabaseKeysWithIdsTest extends UnitTest with TestObjects {

  test("keysWithIds") {
    withCouchDatabase { database =>
      val nodeInfo1 = newNodeInfo(1001)
      val doc1 = NodeDoc("node:1001", nodeInfo1, None)
      database.save(doc1)

      val nodeInfo2 = newNodeInfo(1002)
      val doc2 = NodeDoc("node:1002", nodeInfo2, None)
      database.save(doc2)

      val keys = database.keysWithIds(Seq("node:1001", "node:1002", "node:1003"), stale = false)
      keys should equal(Seq("node:1001", "node:1002"))
    }
  }

  test("keysWithIds - no results") {
    withCouchDatabase { database =>
      val keys = database.keysWithIds(Seq("node:1001", "node:1002"), stale = false)
      keys shouldBe empty
    }
  }
}
