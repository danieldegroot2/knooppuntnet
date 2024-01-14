package kpn.core.obsolete

import kpn.api.common.data.Node
import kpn.api.common.data.Way
import kpn.core.analysis.Link
import kpn.core.analysis.LinkType
import kpn.core.josm.OldDirection
import kpn.core.josm.RelationMember
import kpn.core.josm.WayConnectionType

class OldLinkBuilder(members: Seq[RelationMember]) {

  def links: Seq[Link] = {

    //    val list = ListBuffer[String]()
    //    ways.flatMap(w => Seq(w.nodes.head.id, w.nodes.last.id)).foreach { id =>
    //      if (!list.contains(id)) {
    //        list += id
    //      }
    //    }
    //    val nodeMap = list.zipWithIndex.map{case(id, index)=> id-> (index + 1).toString}.toMap

    con.toSeq.zipWithIndex.map { case (c, index) =>
      val linkType = c.direction match {
        case OldDirection.FORWARD => LinkType.FORWARD
        case OldDirection.BACKWARD => LinkType.BACKWARD
        case OldDirection.ROUNDABOUT => LinkType.ROUNDABOUT
        case OldDirection.NONE => LinkType.NONE
      }
      // TODO for now first and last are considered always connected, later perhaps look at node definitions if included as route member
      Link(linkType, if (index == 0) true else c.hasLinkPrev, if (index == (con.size - 1)) true else c.hasLinkNext,
        c.isLoop, c.isOnewayLoopForwardPart, c.isOnewayLoopBackwardPart, c.isOnewayHead, c.isOnewayTail, c.invalid
      )
    }
  }

  private val UNCONNECTED = Integer.MIN_VALUE

  private val con: scala.collection.mutable.Seq[WayConnectionType] = scala.collection.mutable.Seq.fill(members.size)(null)

  //println("con size=" + con.size)

  private var firstGroupIdx = 0

  private var lastForwardWay = UNCONNECTED
  private var lastBackwardWay = UNCONNECTED
  private var onewayBeginning = false
  private var lastWct: WayConnectionType = _

  members.zipWithIndex.foreach { case (m, i) =>

    if (!m.isWay) {
      if (i > 0) {
        makeLoopIfNeeded(i - 1)
      }
      con(i) = new WayConnectionType() // MV: Direction.NONE  (invalid instance)
      firstGroupIdx = i
    }
    else {

      var wct = new WayConnectionType(false)
      wct.hasLinkPrev = i > 0 && con(i - 1) != null && con(i - 1).isValid // MV is there a previous that has a direction?
      wct.direction = OldDirection.NONE

      if (m.isOneWay) {
        // MV member has role "forward" or "backward"
        if (lastWct != null && lastWct.isOnewayTail) {
          wct.isOnewayHead = true
        }
        if (lastBackwardWay == UNCONNECTED && lastForwardWay == UNCONNECTED) {
          // Beginning of new oneway
          wct.isOnewayHead = true
          lastForwardWay = i - 1
          lastBackwardWay = i - 1
          onewayBeginning = true
        }
      }

      if (wct.hasLinkPrev) {
        if (lastBackwardWay != UNCONNECTED && lastForwardWay != UNCONNECTED) {
          wct = determineOnewayConnectionType(m, i, wct)
          if (!wct.hasLinkPrev) {
            firstGroupIdx = i
          }
        }

        if (!m.isOneWay) {
          // MV member has role "forward" or "backward"
          wct.direction = determineDirection(i - 1, lastWct.direction, i)
          wct.hasLinkPrev = wct.direction != OldDirection.NONE
        }
      }

      if (!wct.hasLinkPrev) {
        wct.direction = determineDirectionOfFirst(i, m)
        if (m.isOneWay) {
          // MV member has role "forward" or "backward"
          wct.isOnewayLoopForwardPart = true
          lastForwardWay = i
        }
      }

      wct.hasLinkNext = false
      if (lastWct != null) {
        lastWct.hasLinkNext = wct.hasLinkPrev
      }
      con(i) = wct
      lastWct = wct

      if (!wct.hasLinkPrev) {
        if (i > 0) {
          makeLoopIfNeeded(i - 1)
        }
        firstGroupIdx = i
      }
    }
  }

  if (members.nonEmpty) {
    makeLoopIfNeeded(members.size - 1)
  }

  private def makeLoopIfNeeded(i: Int): Unit = {
    var loop = false
    if (i == firstGroupIdx) {
      //is primitive loop
      loop = determineDirection(i, OldDirection.FORWARD, i) == OldDirection.FORWARD
    } else {
      loop = determineDirection(i, con(i).direction, firstGroupIdx) == con(firstGroupIdx).direction
    }
    if (loop) {
      firstGroupIdx to i foreach { j =>
        //for (int j=firstGroupIdx; j <= i; ++j) {
        con(j).isLoop = true
      }
    }
  }

  private def determineDirectionOfFirst(i: Int, m: RelationMember): OldDirection.Value = {
    val result: OldDirection.Value = roundaboutType(m)
    if (result != OldDirection.NONE)
      return result

    if (m.isOneWay) {
      // MV member has role "forward" or "backward"
      if (isBackward(m)) return OldDirection.BACKWARD
      else return OldDirection.FORWARD
    } else {
      /* guess the direction and see if it fits with the next member */
      if (determineDirection(i, OldDirection.FORWARD, i + 1) != OldDirection.NONE) return OldDirection.FORWARD
      if (determineDirection(i, OldDirection.BACKWARD, i + 1) != OldDirection.NONE) return OldDirection.BACKWARD
    }
    OldDirection.NONE
  }

  private def roundaboutType(member: RelationMember): OldDirection.Value = {
    if (member == null || !member.isWay) {
      OldDirection.NONE
    } else {
      if (member.way.tags.has("junction", "roundabout")) {
        OldDirection.ROUNDABOUT
      }
      else {
        OldDirection.NONE
      }
    }
  }

  private def isBackward(member: RelationMember): Boolean = {
    member.role.equals("backward")
  }

  private def determineOnewayConnectionType(m: RelationMember, i: Int, wct: WayConnectionType): WayConnectionType = {

    var dirFW = determineDirection(lastForwardWay, con(lastForwardWay).direction, i)
    var dirBW = OldDirection.NONE

    if (onewayBeginning) {
      if (lastBackwardWay < 0) {
        dirBW = determineDirection(firstGroupIdx, reverse(con(firstGroupIdx).direction), i, reversed = true)
      } else {
        dirBW = determineDirection(lastBackwardWay, con(lastBackwardWay).direction, i, reversed = true)
      }

      if (dirBW != OldDirection.NONE) {
        onewayBeginning = false
      }
    } else {
      dirBW = determineDirection(lastBackwardWay, con(lastBackwardWay).direction, i, reversed = true)
    }

    if (m.isOneWay) {
      if (dirBW != OldDirection.NONE) {
        wct.direction = dirBW
        lastBackwardWay = i
        wct.isOnewayLoopBackwardPart = true
      }
      if (dirFW != OldDirection.NONE) {
        wct.direction = dirFW
        lastForwardWay = i
        wct.isOnewayLoopForwardPart = true
      }
      if (dirFW == OldDirection.NONE && dirBW == OldDirection.NONE) {
        //Not connected to previous
        //                        unconnectPreviousLink(con, i, true)
        //                        unconnectPreviousLink(con, i, false)
        wct.hasLinkPrev = false
        if (m.isOneWay) {
          wct.isOnewayHead = true
          lastForwardWay = i - 1
          lastBackwardWay = i - 1
        } else {
          lastForwardWay = UNCONNECTED
          lastBackwardWay = UNCONNECTED
        }
        onewayBeginning = true
      }

      if (dirFW != OldDirection.NONE && dirBW != OldDirection.NONE) {
        //End of oneway loop
        if (i + 1 < members.size && determineDirection(i, dirFW, i + 1) != OldDirection.NONE) {
          wct.isOnewayLoopBackwardPart = false
          dirBW = OldDirection.NONE
          wct.direction = dirFW
        } else {
          wct.isOnewayLoopForwardPart = false
          dirFW = OldDirection.NONE
          wct.direction = dirBW
        }

        wct.isOnewayTail = true
      }

    } else {
      lastForwardWay = UNCONNECTED
      lastBackwardWay = UNCONNECTED
      if (dirFW == OldDirection.NONE || dirBW == OldDirection.NONE) {
        wct.hasLinkPrev = false
      }
    }
    wct
  }

  private def reverse(dir: OldDirection.Value): OldDirection.Value = {
    if (dir == OldDirection.FORWARD) return OldDirection.BACKWARD
    if (dir == OldDirection.BACKWARD) return OldDirection.FORWARD
    dir
  }

  private def determineDirection(ref_i: Int, ref_direction: OldDirection.Value, k: Int): OldDirection.Value = {
    determineDirection(ref_i, ref_direction, k, reversed = false)
  }

  /**
   * Determines the direction of way k with respect to the way ref_i.
   * The way ref_i is assumed to have the direction ref_direction and
   * to be the predecessor of k.
   *
   * If both ways are not linked in any way, NONE is returned.
   *
   * Else the direction is given as follows:
   * Let the relation be a route of oneway streets, and someone travels them in the given order.
   * Direction is FORWARD if it is legal and BACKWARD if it is illegal to do so for the given way.
   *
   * */
  private def determineDirection(ref_i: Int, ref_direction: OldDirection.Value, k: Int, reversed: Boolean): OldDirection.Value = {

    if (ref_i < 0 || k < 0 || ref_i >= members.size || k >= members.size)
      return OldDirection.NONE

    if (ref_direction == OldDirection.NONE)
      return OldDirection.NONE

    val m_ref = members(ref_i)
    val m = members(k)
    var way_ref: Way = null
    var way: Way = null

    if (m_ref.isWay) {
      way_ref = m_ref.way
    }
    if (m.isWay) {
      way = m.way
    }

    if (way_ref == null || way == null)
      return OldDirection.NONE

    /* the list of nodes the way k can dock to */

    val refNodes = ref_direction match {
      case OldDirection.FORWARD => Seq(way_ref.nodes.last)
      case OldDirection.BACKWARD => Seq(way_ref.nodes.head)
      case OldDirection.ROUNDABOUT => way_ref.nodes
    }

    for (n <- refNodes) {
      if (roundaboutType(members(k)) != OldDirection.NONE) {
        for (nn <- way.nodes) {
          if (n == nn)
            return roundaboutType(members(k))
        }
      } else if (m.isOneWay) {
        // MV member has role "forward" or "backward"
        if (n == firstOnewayNode(m) && !reversed) {
          if (isBackward(m))
            return OldDirection.BACKWARD
          else
            return OldDirection.FORWARD
        }
        if (n == lastOnewayNode(m) && reversed) {
          if (isBackward(m))
            return OldDirection.FORWARD
          else
            return OldDirection.BACKWARD
        }
      } else {
        if (n == way.nodes.head)
          return OldDirection.FORWARD
        if (n == way.nodes.last)
          return OldDirection.BACKWARD
      }
    }
    OldDirection.NONE
  }

  private def firstOnewayNode(m: RelationMember): Node = {
    if (!m.isWay) return null
    if (m.role.equals("backward")) return m.way.nodes.last
    m.way.nodes.head
  }

  private def lastOnewayNode(m: RelationMember): Node = {
    if (!m.isWay) return null
    if (m.role.equals("backward")) return m.way.nodes.head
    m.way.nodes.last
  }
}
