package kpn.server.api.status

import kpn.api.common.status.ActionTimestamp
import kpn.api.common.status.BarChart
import kpn.api.common.status.BarChart2D
import kpn.api.common.status.BarChart2dValue
import kpn.api.common.status.DiskUsage
import kpn.api.common.status.LogPage
import kpn.api.common.status.NameValue
import kpn.api.common.status.PeriodParameters
import kpn.api.common.status.ReplicationStatusPage
import kpn.api.common.status.Status
import kpn.api.common.status.SystemStatusPage
import kpn.api.custom.ApiResponse
import kpn.core.common.TimestampLocal
import kpn.server.repository.AnalysisRepository
import kpn.server.repository.MetricsRepository
import org.springframework.stereotype.Component

@Component
class StatusFacadeImpl(
  analysisRepository: AnalysisRepository,
  metricsRepository: MetricsRepository
) extends StatusFacade {

  override def status(): ApiResponse[Status] = {

    val frontEnd = {
      val used = metricsRepository.lastKnownValue("frontend-disk-space-used")
      val available = metricsRepository.lastKnownValue("frontend-disk-space-available")
      BarChart(
        "day",
        Seq(
          NameValue("Used", Math.round(used.toDouble / 1024 / 1024)),
          NameValue("Free", Math.round(available.toDouble / 1024 / 1024)),
        )
      )
    }

    val database = {
      val used = metricsRepository.lastKnownValue("db-disk-space-used")
      val available = metricsRepository.lastKnownValue("db-disk-space-available")
      BarChart(
        "day",
        Seq(
          NameValue("Used", Math.round(used.toDouble / 1024 / 1024)),
          NameValue("Free", Math.round(available.toDouble / 1024 / 1024)),
        )
      )
    }

    val backEnd = {
      val used = metricsRepository.lastKnownValue("backend-disk-space-used")
      val available = metricsRepository.lastKnownValue("backend-disk-space-available")
      val overpass = metricsRepository.lastKnownValue("backend-disk-space-overpass")
      BarChart(
        "day",
        Seq(
          NameValue("Overpass", Math.round(overpass.toDouble / 1024 / 1024)),
          NameValue("Used", Math.round((used - overpass).toDouble / 1024 / 1024)),
          NameValue("Free", Math.round(available.toDouble / 1024 / 1024)),
        )
      )
    }

    val status = Status(
      ActionTimestamp.now(),
      DiskUsage(
        frontEnd,
        database,
        backEnd
      )
    )

    val response = ApiResponse(analysisRepository.lastUpdated(), 1, Some(status))
    TimestampLocal.localize(response)
    response
  }

  override def statusOk(): ApiResponse[String] = {
    ApiResponse(analysisRepository.lastUpdated(), 1, Some("OK"))
  }

  override def replicationStatus(parameters: PeriodParameters): ApiResponse[ReplicationStatusPage] = {

    val replicationDelays = metricsRepository.query(parameters, "replication-delay", average = true)
    val updateDelays = metricsRepository.query(parameters, "update-delay", average = true)
    val analysisDelays = metricsRepository.query(parameters, "analysis-delay", average = true)

    val delay = BarChart2D(
      parameters.period,
      "",
      "",
      "",
      replicationDelays.indices.map { index =>
        val replicationDelay = replicationDelays(index)
        val updateDelay = updateDelays(index)
        val analysisDelay = analysisDelays(index)
        BarChart2dValue(
          replicationDelay.name,
          Seq(
            NameValue("replication", replicationDelay.value),
            NameValue("update", updateDelay.value - replicationDelay.value),
            NameValue("analysis", analysisDelay.value /*- updateDelay.value*/)
          )
        )
      }
    )

    val analysisDelay = BarChart(parameters.period, analysisDelays)
    val updateDelay = BarChart(parameters.period, updateDelays)
    val replicationDelay = BarChart(parameters.period, replicationDelays)

    val replicationBytes = BarChart(parameters.period, metricsRepository.query(parameters, "replication-bytes"))
    val replicationElements = BarChart(parameters.period, metricsRepository.query(parameters, "replication-elements"))
    val replicationChangeSets = BarChart(parameters.period, metricsRepository.query(parameters, "replication-changesets"))

    val periodTitle = parameters.period match {
      case "year" => parameters.year.toString
      case "month" => f"${parameters.year} ${parameters.month.get}"
      case "week" => f"${parameters.year} ${parameters.week.get}"
      case "day" => f"${parameters.year}-${parameters.month.get}-${parameters.day.get}"
      case "hour" => f"${parameters.year}-${parameters.month.get}-${parameters.day.get} hour: ${parameters.hour.get}"
      case _ => ""
    }

    val previous = parameters.period match {
      case "year" => "previous-yearlink"
      case "month" => "previous-monthlink"
      case "week" => "previous-weeklink"
      case "day" => "previous-daylink"
      case "hour" => "previous-hourlink"
      case _ => ""
    }

    val next = parameters.period match {
      case "year" => "next-yearlink"
      case "month" => "next-monthlink"
      case "week" => "next-weeklink"
      case "day" => "next-daylink"
      case "hour" => "next-hourlink"
      case _ => ""
    }

    ApiResponse(
      None,
      1,
      Some(
        ReplicationStatusPage(
          ActionTimestamp.now(),
          parameters.period,
          periodTitle,
          previous,
          next,
          delay,
          analysisDelay,
          updateDelay,
          replicationDelay,
          replicationBytes,
          replicationElements,
          replicationChangeSets
        )
      )
    )
  }

  override def systemStatus(parameters: PeriodParameters): ApiResponse[SystemStatusPage] = {

    val backendDiskSpaceUsed = BarChart(parameters.period, metricsRepository.query(parameters, "backend-disk-space-used", average = true))
    val backendDiskSpaceAvailable = BarChart(parameters.period, metricsRepository.query(parameters, "backend-disk-space-available", average = true))
    val backendDiskSpaceOverpass = BarChart(parameters.period, metricsRepository.query(parameters, "backend-disk-space-overpass", average = true))

    val analysisDocCount = BarChart(parameters.period, metricsRepository.query(parameters, "backend-analysis-docs", average = true))
    val analysisDiskSize = BarChart(parameters.period, metricsRepository.query(parameters, "backend-analysis-disk-size", average = true))
    val analysisDiskSizeExternal = BarChart(parameters.period, metricsRepository.query(parameters, "backend-analysis-data-size-external", average = true))
    val analysisDataSize = BarChart(parameters.period, metricsRepository.query(parameters, "backend-analysis-data-size", average = true))

    val changesDocCount = BarChart(parameters.period, metricsRepository.query(parameters, "backend-changes-docs", average = true))
    val changesDiskSize = BarChart(parameters.period, metricsRepository.query(parameters, "backend-changes-disk-size", average = true))
    val changesDiskSizeExternal = BarChart(parameters.period, metricsRepository.query(parameters, "backend-changes-data-size-external", average = true))
    val changesDataSize = BarChart(parameters.period, metricsRepository.query(parameters, "backend-changes-data-size", average = true))

    val periodTitle = parameters.period match {
      case "year" => parameters.year.toString
      case "month" => f"${parameters.year} ${parameters.month.get}"
      case "week" => f"${parameters.year} ${parameters.week.get}"
      case "day" => f"${parameters.year}-${parameters.month.get}-${parameters.day.get}"
      case "hour" => f"${parameters.year}-${parameters.month.get}-${parameters.day.get} hour: ${parameters.hour.get}"
      case _ => ""
    }

    val previous = parameters.period match {
      case "year" => "previous-yearlink"
      case "month" => "previous-monthlink"
      case "week" => "previous-weeklink"
      case "day" => "previous-daylink"
      case "hour" => "previous-hourlink"
      case _ => ""
    }

    val next = parameters.period match {
      case "year" => "next-yearlink"
      case "month" => "next-monthlink"
      case "week" => "next-weeklink"
      case "day" => "next-daylink"
      case "hour" => "next-hourlink"
      case _ => ""
    }

    ApiResponse(
      None,
      1,
      Some(
        SystemStatusPage(
          ActionTimestamp.now(),
          parameters.period,
          periodTitle,
          previous,
          next,
          backendDiskSpaceUsed,
          backendDiskSpaceAvailable,
          backendDiskSpaceOverpass,
          analysisDocCount,
          analysisDiskSize,
          analysisDiskSizeExternal,
          analysisDataSize,
          changesDocCount,
          changesDiskSize,
          changesDiskSizeExternal,
          changesDataSize
        )
      )
    )
  }

  override def logStatus(parameters: PeriodParameters): ApiResponse[LogPage] = {

    val tile = BarChart(parameters.period, metricsRepository.query(parameters, "tile"))
    val tileRobot = BarChart(parameters.period, metricsRepository.query(parameters, "tile-robot"))
    val api = BarChart(parameters.period, metricsRepository.query(parameters, "api"))
    val apiRobot = BarChart(parameters.period, metricsRepository.query(parameters, "api-robot"))
    val analysis = BarChart(parameters.period, metricsRepository.query(parameters, "analysis"))
    val analysisRobot = BarChart(parameters.period, metricsRepository.query(parameters, "analysis-robot"))
    val robot = BarChart(parameters.period, metricsRepository.query(parameters, "robot"))
    val nonRobot = BarChart(parameters.period, metricsRepository.query(parameters, "non-robot"))

    val periodTitle = parameters.period match {
      case "year" => parameters.year.toString
      case "month" => f"${parameters.year} ${parameters.month.get}"
      case "week" => f"${parameters.year} ${parameters.week.get}"
      case "day" => f"${parameters.year}-${parameters.month.get}-${parameters.day.get}"
      case "hour" => f"${parameters.year}-${parameters.month.get}-${parameters.day.get} hour: ${parameters.hour.get}"
      case _ => ""
    }

    val previous = parameters.period match {
      case "year" => "previous-yearlink"
      case "month" => "previous-monthlink"
      case "week" => "previous-weeklink"
      case "day" => "previous-daylink"
      case "hour" => "previous-hourlink"
      case _ => ""
    }

    val next = parameters.period match {
      case "year" => "next-yearlink"
      case "month" => "next-monthlink"
      case "week" => "next-weeklink"
      case "day" => "next-daylink"
      case "hour" => "next-hourlink"
      case _ => ""
    }

    ApiResponse(
      None,
      1,
      Some(
        LogPage(
          ActionTimestamp.now(),
          parameters.period,
          periodTitle,
          previous,
          next,
          tile,
          tileRobot,
          api,
          apiRobot,
          analysis,
          analysisRobot,
          robot,
          nonRobot
        )
      )
    )
  }
}
