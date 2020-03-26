package kpn.core.database.views.action

import kpn.api.common.status.NameValue
import kpn.api.common.status.PeriodParameters
import kpn.core.database.Database
import kpn.core.database.query.Fields
import kpn.core.database.query.Query
import kpn.core.database.views.common.View

object BackendActionView extends View {

  private case class ViewResultRow(key: Seq[String], value: Seq[Long])

  private case class ViewResult(rows: Seq[ViewResultRow])

  override def reduce: Option[String] = sumAndCount

  def query(database: Database, parameters: PeriodParameters, action: String, average: Boolean, stale: Boolean = true): Seq[NameValue] = {
    parameters.period match {
      case "year" => queryYear(database, parameters, action, average, stale)
      case "month" => queryMonth(database, parameters, action, average, stale)
      case "week" => queryWeek(database, parameters, action, average, stale)
      case "day" => queryDay2(database, parameters, action, average, stale)
      case "hour" => queryHour(database, parameters, action, average, stale)
      case _ => Seq()
    }
  }

  private def queryYear(database: Database, parameters: PeriodParameters, action: String, average: Boolean, stale: Boolean = true): Seq[NameValue] = {

    val query = Query(BackendActionDesign, BackendActionView, classOf[ViewResult])
      .keyStartsWith("week", action, parameters.year)
      .reduce(true)
      .groupLevel(4)
      .stale(stale)
    val result = database.execute(query)
    val nameValues = result.rows.map { row =>
      val key = Fields(row.key)
      val week = key.string(3)
      val sum = row.value.head
      val count = row.value(1)
      NameValue(week, if (average) sum / count else sum)
    }
    val nameValueMap = nameValues.map(nv => nv.name -> nv).toMap
    val weekCount = 53 // TODO get correct week count for specified year
    (1 to weekCount).map { week =>
      val weekString = week.toString
      nameValueMap.get(weekString) match {
        case Some(nameValue) => nameValue
        case None => NameValue(weekString, 0)
      }
    }
  }

  private def queryMonth(database: Database, parameters: PeriodParameters, action: String, average: Boolean, stale: Boolean = true): Seq[NameValue] = {

    val query = Query(BackendActionDesign, BackendActionView, classOf[ViewResult])
      .keyStartsWith("time", action, parameters.year, parameters.month.get)
      .reduce(true)
      .groupLevel(5)
      .stale(stale)
    val result = database.execute(query)
    val nameValues = result.rows.map { row =>
      val key = Fields(row.key)
      val day = key.string(4)
      val sum = row.value.head
      val count = row.value(1)
      NameValue(day, if (average) sum / count else sum)
    }
    val nameValueMap = nameValues.map(nv => nv.name -> nv).toMap
    val dayCount = 31 // TODO get correct day count for specified month (taking into account leap year, etc.)
    (1 to dayCount).map { day =>
      val dayString = day.toString
      nameValueMap.get(dayString) match {
        case Some(nameValue) => nameValue
        case None => NameValue(dayString, 0)
      }
    }
  }

  private def queryWeek(database: Database, parameters: PeriodParameters, action: String, average: Boolean, stale: Boolean = true): Seq[NameValue] = {

    val query = Query(BackendActionDesign, BackendActionView, classOf[ViewResult])
      .keyStartsWith("week", action, parameters.year, parameters.week.get)
      .reduce(true)
      .groupLevel(5)
      .stale(stale)
    val result = database.execute(query)
    val nameValues = result.rows.map { row =>
      val key = Fields(row.key)
      val day = key.string(4)
      val sum = row.value.head
      val count = row.value(1)
      NameValue(day, if (average) sum / count else sum)
    }
    val nameValueMap = nameValues.map(nv => nv.name -> nv).toMap
    (1 to 7).map { day =>
      val dayString = day.toString
      nameValueMap.get(dayString) match {
        case Some(nameValue) => nameValue
        case None => NameValue(dayString, 0)
      }
    }
  }

  private def queryDay2(database: Database, parameters: PeriodParameters, action: String, average: Boolean, stale: Boolean = true): Seq[NameValue] = {

    val query = Query(BackendActionDesign, BackendActionView, classOf[ViewResult])
      .keyStartsWith("time", action, parameters.year, parameters.month.get, parameters.day.get)
      .reduce(true)
      .groupLevel(6)
      .stale(stale)
    val result = database.execute(query)
    val nameValues = result.rows.map { row =>
      val key = Fields(row.key)
      val hour = f"${key.int(5)}%02d"
      val sum = row.value.head
      val count = row.value(1)
      NameValue(hour, if (average) sum / count else sum)
    }
    val nameValueMap = nameValues.map(nv => nv.name -> nv).toMap
    (0 to 23).map { hour =>
      val hourString = f"${hour}%02d"
      nameValueMap.get(hourString) match {
        case Some(nameValue) => nameValue
        case None => NameValue(hourString, 0)
      }
    }
  }

  private def queryHour(database: Database, parameters: PeriodParameters, action: String, average: Boolean, stale: Boolean = true): Seq[NameValue] = {

    val query = Query(BackendActionDesign, BackendActionView, classOf[ViewResult])
      .keyStartsWith("time", action, parameters.year, parameters.month.get, parameters.day.get, parameters.hour.get)
      .reduce(true)
      .groupLevel(7)
      .stale(stale)
    val result = database.execute(query)
    val nameValues = result.rows.map { row =>
      val key = Fields(row.key)
      val minute = f"${key.int(6)}%02d"
      val sum = row.value.head
      val count = row.value(1)
      NameValue(minute, if (average) sum / count else sum)
    }
    val nameValueMap = nameValues.map(nv => nv.name -> nv).toMap
    (0 to 59).map { minute =>
      val minuteString = f"${minute}%02d"
      nameValueMap.get(minuteString) match {
        case Some(nameValue) => nameValue
        case None => NameValue(minuteString, 0)
      }
    }
  }

}
