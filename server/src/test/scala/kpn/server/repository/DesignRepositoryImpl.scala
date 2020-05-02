package kpn.server.repository

import kpn.core.database.Database
import kpn.core.database.doc.DesignDoc
import kpn.core.database.doc.ViewDoc
import kpn.core.database.views.common.Design
import kpn.core.util.Util

class DesignRepositoryImpl(database: Database) extends DesignRepository {

  def save(design: Design): Unit = {
    val views = design.views.map(v => v.name -> ViewDoc(v.map, v.reduce)).toMap
    val id = "_design/" + Util.classNameOf(design)
    database.save(DesignDoc(id, "javascript", views))
  }
}
