// Migrated to Angular: overview-table.component.ts
package kpn.client.components.overview

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.Implicits._
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^.<
import japgolly.scalajs.react.vdom.html_<^.^
import kpn.client.common.Context
import kpn.client.common.Nls.nls
import kpn.client.components.common.UiNetworkTypeIcon
import kpn.shared.Fact
import kpn.shared.NetworkType
import kpn.shared.statistics.Statistics
import scalacss.ScalaCssReact._
import scalacss.internal.StyleA

object UiOverviewTable {

  private case class Props(context: Context, statistics: Statistics)

  private val component = ScalaComponent.builder[Props]("overview-table")
    .render_P { props =>
      new Renderer(props.statistics)(props.context).render()
    }
    .build

  def apply(statistics: Statistics)(implicit context: Context): VdomElement = component(Props(context, statistics))

  private class Renderer(statistics: Statistics)(implicit context: Context) {

    private val infos = new OverviewInfos(statistics)

    def render(): VdomElement = {

      <.table(
        header(),
        <.tbody(
          TagMod(
            infoRow(infos.lengthInfo),
            infoRow(infos.networkCountInfo),
            infoRow(infos.nodeCountInfo),
            infoRow(infos.routeCountInfo),
            routeNotContinious(),
            brokenRoutes().toTagMod,
            infoRow(infos.routeIncompleteInfo),
            infoRow(infos.routeIncompleteOkInfo),
            infoRow(infos.routeFixmetodoInfo),
            infoRow(infos.orphanNodeCountInfo),
            orphanRoutes().toTagMod,
            integrityCheck().toTagMod,
            infoRow(infos.routeUnusedSegmentsInfo),
            infoRow(infos.routeNodeMissingInWaysInfo),
            infoRow(infos.routeRedundantNodesInfo),
            infoRow(infos.routeWithoutWaysInfo),
            infoRow(infos.routeNameMissingInfo),
            infoRow(infos.routeTagMissingInfo),
            infoRow(infos.routeTagInvalidInfo),
            infoRow(infos.routeUnexpectedNode),
            infoRow(infos.routeUnexpectedRelation),
            infoRow(infos.networkExtraMemberNode),
            infoRow(infos.networkExtraMemberWay),
            infoRow(infos.networkExtraMemberRelation),
            infoRow(infos.nodeMemberMissing),
            infoRow(infos.nameMissingInfo),
            infoRow(infos.routeUnaccessibleInfo),
            infoRow(infos.routeInvalidSortingOrderInfo),
            infoRow(infos.routeNodeNameMismatchInfo)
          )
        )
      )
    }

    private def routeNotContinious(): TagMod = {
      Seq(
        <.tr(
          <.td(
            ^.rowSpan := 3,
            nls(Fact.RouteNotContinious.name, Fact.RouteNotContinious.nlName)
          ),
          counts(infos.routeNotContiniousNetworkCountInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.routeNotContiniousNetworkCountInfo.comment
          )
        ),
        <.tr(
          counts(infos.routeNotContiniousInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.routeNotContiniousInfo.comment
          )
        ),
        <.tr(
          counts(infos.routeNotContiniousPercentageInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.routeNotContiniousPercentageInfo.comment
          )
        )
      ).toTagMod
    }

    private def brokenRoutes(): Seq[VdomElement] = {
      Seq(
        <.tr(
          <.td(
            ^.rowSpan := 3,
            nls("Broken routes", "Routes met opmerkingen")
          ),
          counts(infos.routeBrokenNetworkCountInfo),
          <.td(infos.routeBrokenNetworkCountInfo.comment)
        ),
        <.tr(
          counts(infos.routeBrokenCountInfo),
          <.td(infos.routeBrokenCountInfo.comment)
        ),
        <.tr(
          counts(infos.routeBrokenPercentageInfo),
          <.td(infos.routeBrokenPercentageInfo.comment)
        )
      )
    }

    private def orphanRoutes(): Seq[VdomElement] = {
      Seq(
        <.tr(
          <.td(
            ^.rowSpan := 2,
            infos.orphanRouteCountInfo.title
          ),
          counts(infos.orphanRouteCountInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.orphanRouteCountInfo.comment)
        ),
        <.tr(
          counts(infos.orphanRouteKmInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.orphanRouteKmInfo.comment
          )
        )
      )
    }

    private def integrityCheck(): Seq[VdomElement] = {
      Seq(
        <.tr(
          <.td(
            ^.rowSpan := 5,
            nls("Integrity check", "Integriteit")
          ),
          counts(infos.integrityCheckNetworkCountInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.integrityCheckNetworkCountInfo.comment
          )
        ),
        <.tr(
          counts(infos.integrityCheckCount),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.integrityCheckCount.comment
          )
        ),
        <.tr(
          counts(infos.integrityCheckFailedCount),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.integrityCheckFailedCount.comment
          )
        ),
        <.tr(
          counts(infos.integrityCheckPassRateInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.integrityCheckPassRateInfo.comment
          )
        ),
        <.tr(
          counts(infos.integrityCheckCoverageInfo),
          <.td(
            UiOverviewPage.Styles.commentCell,
            infos.integrityCheckCoverageInfo.comment
          )
        )
      )
    }

    private def infoRow(info: UiOverviewInfo, titleStyle: StyleA = UiOverviewPage.Styles.regularTitle): VdomElement = {
      <.tr(
        <.td(
          titleStyle,
          info.title
        ),
        counts(info),
        <.td(
          UiOverviewPage.Styles.commentCell,
          info.comment
        )
      )
    }

    private def counts(info: UiOverviewInfo): TagMod = {
      Seq(
        <.td(info.counts.total),
        <.td(info.counts.nlRcn),
        <.td(info.counts.nlRwn),
        <.td(info.counts.nlRhn),
        <.td(info.counts.nlRmn),
        <.td(info.counts.nlRpn),
        <.td(info.counts.nlRin),
        <.td(info.counts.beRcn),
        <.td(info.counts.beRwn),
        <.td(info.counts.beRhn),
        <.td(info.counts.deRcn),
        <.td(info.counts.deRwn),
        <.td(info.counts.frRcn),
        <.td(info.counts.frRwn)
      ).toTagMod
    }

    private def header(): VdomElement = {
      <.thead(
        <.tr(
          headerCell("Detail"),
          headerCell(nls("Total", "Totaal")),
          headerCellCountry(6, nls("The Netherlands", "Nederland")),
          headerCellCountry(3, nls("Belgium", "België")),
          headerCellCountry(2, nls("Germany", "Duitsland")),
          headerCellCountry(2, nls("France", "Frankrijk")),
          headerCell(nls("Comment", "Commentaar"))
        ),
        <.tr(
          headerCellNetworkType(NetworkType.bicycle),
          headerCellNetworkType(NetworkType.hiking),
          headerCellNetworkType(NetworkType.horseRiding),
          headerCellNetworkType(NetworkType.motorboat),
          headerCellNetworkType(NetworkType.canoe),
          headerCellNetworkType(NetworkType.inlineSkates),
          headerCellNetworkType(NetworkType.bicycle),
          headerCellNetworkType(NetworkType.hiking),
          headerCellNetworkType(NetworkType.horseRiding),
          headerCellNetworkType(NetworkType.bicycle),
          headerCellNetworkType(NetworkType.hiking),
          headerCellNetworkType(NetworkType.bicycle),
          headerCellNetworkType(NetworkType.hiking)
        )
      )
    }

    private def headerCell(title: String): VdomElement = {
      <.th(
        ^.rowSpan := 2,
        title
      )
    }

    private def headerCellCountry(colSpan: Int, country: String): VdomElement = {
      <.th(
        ^.colSpan := colSpan,
        country
      )
    }

    private def headerCellNetworkType(networkType: NetworkType): VdomElement = {
      <.th(
        UiOverviewPage.Styles.valueCell,
        UiNetworkTypeIcon.apply(networkType)
      )
    }
  }

}
