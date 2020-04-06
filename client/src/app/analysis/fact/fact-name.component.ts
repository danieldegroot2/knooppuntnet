import {Component, Input} from "@angular/core";

@Component({
  selector: "kpn-fact-name",
  template: `
    <ng-container [ngSwitch]="factName">
      <ng-container i18n="@@fact.name.added" *ngSwitchCase="'Added'">Added</ng-container>
      <ng-container i18n="@@fact.name.become-orphan" *ngSwitchCase="'BecomeOrphan'">BecomeOrphan</ng-container>
      <ng-container i18n="@@fact.name.deleted" *ngSwitchCase="'Deleted'">Deleted</ng-container>
      <ng-container i18n="@@fact.name.integrity-check" *ngSwitchCase="'IntegrityCheck'">IntegrityCheck</ng-container>
      <ng-container i18n="@@fact.name.integrity-check-failed" *ngSwitchCase="'IntegrityCheckFailed'">IntegrityCheckFailed</ng-container>
      <ng-container i18n="@@fact.name.lost-bicycle-node-tag" *ngSwitchCase="'LostBicycleNodeTag'">LostBicycleNodeTag</ng-container>
      <ng-container i18n="@@fact.name.lost-hiking-node-tag" *ngSwitchCase="'LostHikingNodeTag'">LostHikingNodeTag</ng-container>
      <ng-container i18n="@@fact.name.lost-route-tags" *ngSwitchCase="'LostRouteTags'">LostRouteTags</ng-container>
      <ng-container i18n="@@fact.name.name-missing" *ngSwitchCase="'NameMissing'">NameMissing</ng-container>
      <ng-container i18n="@@fact.name.network-extra-member-node" *ngSwitchCase="'NetworkExtraMemberNode'">NetworkExtraMemberNode</ng-container>
      <ng-container i18n="@@fact.name.network-extra-member-relation" *ngSwitchCase="'NetworkExtraMemberRelation'">NetworkExtraMemberRelation</ng-container>
      <ng-container i18n="@@fact.name.network-extra-member-way" *ngSwitchCase="'NetworkExtraMemberWay'">NetworkExtraMemberWay</ng-container>
      <ng-container i18n="@@fact.name.node-member-missing" *ngSwitchCase="'NodeMemberMissing'">NodeMemberMissing</ng-container>
      <ng-container i18n="@@fact.name.orphan-node" *ngSwitchCase="'OrphanNode'">OrphanNode</ng-container>
      <ng-container i18n="@@fact.name.orphan-route" *ngSwitchCase="'OrphanRoute'">OrphanRoute</ng-container>
      <ng-container i18n="@@fact.name.route-analysis-failed" *ngSwitchCase="'RouteAnalysisFailed'">RouteAnalysisFailed</ng-container>
      <ng-container i18n="@@fact.name.route-broken" *ngSwitchCase="'RouteBroken'">RouteBroken</ng-container>
      <ng-container i18n="@@fact.name.route-fixmetodo" *ngSwitchCase="'RouteFixmetodo'">RouteFixmetodo</ng-container>
      <ng-container i18n="@@fact.name.route-incomplete" *ngSwitchCase="'RouteIncomplete'">RouteIncomplete</ng-container>
      <ng-container i18n="@@fact.name.route-incomplete-ok" *ngSwitchCase="'RouteIncompleteOk'">RouteIncompleteOk</ng-container>
      <ng-container i18n="@@fact.name.route-invalid-sorting-order" *ngSwitchCase="'RouteInvalidSortingOrder'">RouteInvalidSortingOrder</ng-container>
      <ng-container i18n="@@fact.name.route-name-missing" *ngSwitchCase="'RouteNameMissing'">RouteNameMissing</ng-container>
      <ng-container i18n="@@fact.name.route-node-missing-in-ways" *ngSwitchCase="'RouteNodeMissingInWays'">RouteNodeMissingInWays</ng-container>
      <ng-container i18n="@@fact.name.route-node-name-mismatch" *ngSwitchCase="'RouteNodeNameMismatch'">RouteNodeNameMismatch</ng-container>
      <ng-container i18n="@@fact.name.route-not-backward" *ngSwitchCase="'RouteNotBackward'">RouteNotBackward</ng-container>
      <ng-container i18n="@@fact.name.route-not-continious" *ngSwitchCase="'RouteNotContinious'">RouteNotContinious</ng-container>
      <ng-container i18n="@@fact.name.route-not-forward" *ngSwitchCase="'RouteNotForward'">RouteNotForward</ng-container>
      <ng-container i18n="@@fact.name.route-not-one-way" *ngSwitchCase="'RouteNotOneWay'">RouteNotOneWay</ng-container>
      <ng-container i18n="@@fact.name.route-one-way" *ngSwitchCase="'RouteOneWay'">RouteOneWay</ng-container>
      <ng-container i18n="@@fact.name.route-overlapping-ways" *ngSwitchCase="'RouteOverlappingWays'">RouteOverlappingWays</ng-container>
      <ng-container i18n="@@fact.name.route-redundant-nodes" *ngSwitchCase="'RouteRedundantNodes'">RouteRedundantNodes</ng-container>
      <ng-container i18n="@@fact.name.route-reversed" *ngSwitchCase="'RouteReversed'">RouteReversed</ng-container>
      <ng-container i18n="@@fact.name.route-suspicious-ways" *ngSwitchCase="'RouteSuspiciousWays'">RouteSuspiciousWays</ng-container>
      <ng-container i18n="@@fact.name.route-tag-invalid" *ngSwitchCase="'RouteTagInvalid'">RouteTagInvalid</ng-container>
      <ng-container i18n="@@fact.name.route-tag-missing" *ngSwitchCase="'RouteTagMissing'">RouteTagMissing</ng-container>
      <ng-container i18n="@@fact.name.route-unaccessible" *ngSwitchCase="'RouteUnaccessible'">RouteUnaccessible</ng-container>
      <ng-container i18n="@@fact.name.route-unexpected-node" *ngSwitchCase="'RouteUnexpectedNode'">RouteUnexpectedNode</ng-container>
      <ng-container i18n="@@fact.name.route-unexpected-relation" *ngSwitchCase="'RouteUnexpectedRelation'">RouteUnexpectedRelation</ng-container>
      <ng-container i18n="@@fact.name.route-unused-segments" *ngSwitchCase="'RouteUnusedSegments'">RouteUnusedSegments</ng-container>
      <ng-container i18n="@@fact.name.route-without-ways" *ngSwitchCase="'RouteWithoutWays'">RouteWithoutWays</ng-container>
      <ng-container i18n="@@fact.name.was-orphan" *ngSwitchCase="'WasOrphan'">WasOrphan</ng-container>
      <ng-container *ngSwitchDefault>?{{factName}}?</ng-container>
    </ng-container>
  `
})
export class FactNameComponent {
  @Input() factName: string;
}
