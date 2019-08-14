import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {flatMap, map, tap} from "rxjs/operators";
import {AppService} from "../../../app.service";
import {ApiResponse} from "../../../kpn/shared/api-response";
import {NetworkFactsPage} from "../../../kpn/shared/network/network-facts-page";
import {NetworkCacheService} from "../../../services/network-cache.service";
import {Subscriptions} from "../../../util/Subscriptions";
import {FactLevel} from "../../fact/fact-level";
import {Facts} from "../../fact/facts";

@Component({
  selector: "kpn-network-facts-page",
  template: `

    <kpn-network-page-header
      [networkId]="networkId"
      pageTitle="Facts"
      i18n-pageTitle="@@network-facts.title">
    </kpn-network-page-header>

    <div *ngIf="response">
      <div *ngIf="!page" i18n="@@network-facts.network-not-found">
        Network not found
      </div>
      <div *ngIf="page">

        <kpn-situation-on [timestamp]="response.situationOn"></kpn-situation-on>

        <!--@@ Geen feiten -->
        <div *ngIf="!hasFacts()" i18n="@@network-facts.no-facts">
          No facts
        </div>

        <kpn-items *ngIf="hasFacts()">

          <kpn-item *ngFor="let fact of page.facts; let i=index" [index]="nextIndex()">
            <kpn-fact-header [factName]="fact.name"></kpn-fact-header>
          </kpn-item>


          <kpn-item *ngIf="page.networkFacts.nameMissing" [index]="nextIndex()">
            <kpn-fact-header [factName]="'NetworkNameMissing'"></kpn-fact-header>
          </kpn-item>


          <kpn-item *ngIf="!page.networkFacts.networkExtraMemberNode.isEmpty()" [index]="nextIndex()">
            <kpn-fact-header
              [factName]="'NetworkExtraMemberNode'"
              [factCount]="page.networkFacts.networkExtraMemberNode.size">
            </kpn-fact-header>
            <div *ngFor="let fact of page.networkFacts.networkExtraMemberNode">
              <kpn-osm-link-node [nodeId]="fact.memberId" [title]="fact.memberId.toString()"></kpn-osm-link-node>
              (
              <kpn-josm-node [nodeId]="fact.memberId"></kpn-josm-node>
              )
            </div>
          </kpn-item>


          <kpn-item *ngIf="!page.networkFacts.networkExtraMemberWay.isEmpty()" [index]="nextIndex()">
            <kpn-fact-header
              [factName]="'NetworkExtraMemberWay'"
              [factCount]="page.networkFacts.networkExtraMemberWay.size">
            </kpn-fact-header>
            <div *ngFor="let fact of page.networkFacts.networkExtraMemberWay">
              <kpn-osm-link-way [wayId]="fact.memberId" [title]="fact.memberId.toString()"></kpn-osm-link-way>
              (
              <kpn-josm-way [wayId]="fact.memberId"></kpn-josm-way>
              )
            </div>
          </kpn-item>


          <kpn-item *ngIf="!page.networkFacts.networkExtraMemberRelation.isEmpty()" [index]="nextIndex()">
            <kpn-fact-header
              [factName]="'NetworkExtraMemberRelation'"
              [factCount]="page.networkFacts.networkExtraMemberRelation.size">
            </kpn-fact-header>
            <div *ngFor="let fact of page.networkFacts.networkExtraMemberRelation">
              <kpn-osm-link-relation [relationId]="fact.memberId" [title]="fact.memberId.toString()"></kpn-osm-link-relation>
              (
              <kpn-josm-relation [relationId]="fact.memberId"></kpn-josm-relation>
              )
            </div>
          </kpn-item>


          <kpn-item *ngIf="page.networkFacts.integrityCheckFailed" [index]="nextIndex()">
            <kpn-network-fact-integrity-check-failed
              [integrityCheckFailed]="page.networkFacts.integrityCheckFailed">
            </kpn-network-fact-integrity-check-failed>
          </kpn-item>


          <kpn-item *ngFor="let fact of page.nodeFacts" [index]="nextIndex()">
            <kpn-fact-header
              [factName]="fact.fact.name"
              [factCount]="fact.nodes.size">
            </kpn-fact-header>
            <div *ngIf="fact.nodes.size == 1">
              <span i18n="TODO">Node</span>:
            </div>
            <div *ngIf="fact.nodes.size > 1">
              <span i18n="TODO">Nodes</span>:
            </div>
            <div class="kpn-comma-list">
              <span *ngFor="let ref of fact.nodes">
                <kpn-link-node [nodeId]="ref.id" [nodeName]="ref.name"></kpn-link-node>
              </span>
            </div>
          </kpn-item>


          <kpn-item *ngFor="let fact of page.routeFacts" [index]="nextIndex()">
            <kpn-fact-header
              [factName]="fact.fact.name"
              [factCount]="fact.routes.size">
            </kpn-fact-header>
            <div *ngIf="fact.routes.size == 1">
              <span i18n="TODO">Route</span>:
            </div>
            <div *ngIf="fact.routes.size > 1">
              <span i18n="TODO">Routes</span>:
            </div>
            <div class="kpn-comma-list">
              <span *ngFor="let ref of fact.routes">
                <kpn-link-node [nodeId]="ref.id" [nodeName]="ref.name"></kpn-link-node>
              </span>
            </div>
          </kpn-item>

        </kpn-items>
      </div>
      <kpn-json [object]="response"></kpn-json>
    </div>
  `
})
export class NetworkFactsPageComponent implements OnInit, OnDestroy {

  networkId: number;
  response: ApiResponse<NetworkFactsPage>;
  private readonly subscriptions = new Subscriptions();

  private itemIndex = 0;

  constructor(private activatedRoute: ActivatedRoute,
              private appService: AppService,
              private networkCacheService: NetworkCacheService) {
  }

  get page(): NetworkFactsPage {
    return this.response.result;
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.activatedRoute.params.pipe(
        map(params => +params["networkId"]),
        tap(networkId => this.networkId = networkId),
        flatMap(networkId => this.appService.networkFacts(networkId))
      ).subscribe(response => this.processResponse(response))
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  hasFacts(): boolean {
    // return this.hasNetworkFacts() ||
    //   !this.page.nodeFacts.isEmpty() ||
    //   !this.page.routeFacts.isEmpty() ||
    //   !this.page.facts.isEmpty();

    return true;
  }

  nextIndex(): number {
    return this.itemIndex++;
  }

  private hasNetworkFacts(): boolean {
    return !!this.page.networkFacts && (
      !this.page.networkFacts.networkExtraMemberNode.isEmpty() ||
      !this.page.networkFacts.networkExtraMemberWay.isEmpty() ||
      !this.page.networkFacts.networkExtraMemberRelation.isEmpty() ||
      !!this.page.networkFacts.integrityCheckFailed ||
      !!this.page.networkFacts.nameMissing
    );
  }

  private processResponse(response: ApiResponse<NetworkFactsPage>) {
    this.response = response;
    this.itemIndex = 0;
    if (this.page) {
      this.networkCacheService.setNetworkSummary(this.networkId, this.page.networkSummary);
      this.networkCacheService.setNetworkName(this.networkId, this.page.networkSummary.name);
    }
  }

}
