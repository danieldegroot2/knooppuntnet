import { Input } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { SubsetFactDetailsPage } from '@api/common/subset/subset-fact-details-page';
import { List } from 'immutable';
import { Util } from '../../../components/shared/util';

@Component({
  selector: 'kpn-subset-fact-details',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="page.networks.length === 0" class="kpn-line">
      <span i18n="@@subset-facts.no-facts">No facts</span>
      <kpn-icon-happy></kpn-icon-happy>
    </div>
    <div *ngIf="page.networks.length > 0">
      <div class="kpn-space-separated kpn-label">
        <span>{{ refCount() }}</span>
        <span *ngIf="hasNodeRefs()" i18n="@@subset-facts.node-refs"
          >{refCount(), plural, one {node} other {nodes}}</span
        >
        <span *ngIf="hasRouteRefs()" i18n="@@subset-facts.route-refs"
          >{refCount(), plural, one {route} other {routes}}</span
        >
        <span *ngIf="hasOsmNodeRefs()" i18n="@@subset-facts.osm-node-refs"
          >{refCount(), plural, one {node} other {nodes}}</span
        >
        <span *ngIf="hasOsmWayRefs()" i18n="@@subset-facts.osm-way-refs"
          >{refCount, plural, one {way} other {ways}}</span
        >
        <span
          *ngIf="hasOsmRelationRefs()"
          i18n="@@subset-facts.osm-relation-refs"
          >{refCount, plural, one {relation} other {relations}}</span
        >
        <span i18n="@@subset-facts.in-networks"
          >{page.networks.length, plural, one {in 1 network} other {in
          {{ page.networks.length }} networks}}</span
        >
      </div>

      <kpn-items>
        <kpn-item
          *ngFor="let networkFactRefs of page.networks; let i = index"
          [index]="i"
        >
          <div class="fact-detail">
            <span
              *ngIf="networkFactRefs.networkId === 0"
              i18n="@@subset-facts.orphan-routes"
              >Free routes</span
            >
            <a
              *ngIf="networkFactRefs.networkId !== 0"
              [routerLink]="'/analysis/network/' + networkFactRefs.networkId"
            >
              {{ networkFactRefs.networkName }}
            </a>
          </div>
          <div class="fact-detail">
            <span
              *ngIf="hasNodeRefs()"
              i18n="@@subset-facts.nodes"
              class="kpn-label"
              >{networkFactRefs.factRefs.length, plural, one {1 node} other
              {{{networkFactRefs.factRefs.length}} nodes}}</span
            >
            <span
              *ngIf="hasRouteRefs()"
              i18n="@@subset-facts.routes"
              class="kpn-label"
              >{networkFactRefs.factRefs.length, plural, one {1 route} other
              {{{networkFactRefs.factRefs.length}} routes}}</span
            >
          </div>
          <div class="kpn-comma-list fact-detail">
            <span *ngFor="let ref of networkFactRefs.factRefs">
              <kpn-link-node
                *ngIf="hasNodeRefs()"
                [nodeId]="ref.id"
                [nodeName]="ref.name"
              ></kpn-link-node>
              <kpn-link-route
                *ngIf="hasRouteRefs()"
                [routeId]="ref.id"
                [title]="ref.name"
                [networkType]="page.subsetInfo.networkType"
              ></kpn-link-route>
              <kpn-osm-link-node
                *ngIf="hasOsmNodeRefs()"
                [nodeId]="ref.id"
                [title]="ref.name"
              ></kpn-osm-link-node>
              <kpn-osm-link-way
                *ngIf="hasOsmWayRefs()"
                [wayId]="ref.id"
                [title]="ref.name"
              ></kpn-osm-link-way>
              <kpn-osm-link-relation
                *ngIf="hasOsmRelationRefs()"
                [relationId]="ref.id"
                [title]="ref.name"
              ></kpn-osm-link-relation>
            </span>
          </div>
        </kpn-item>
      </kpn-items>
    </div>
  `,
  styleUrls: ['./_subset-fact-details-page.component.scss'],
})
export class SubsetFactDetailsComponent {
  @Input() page: SubsetFactDetailsPage;

  factName(): string {
    return this.page.fact;
  }

  refCount(): number {
    return Util.sum(List(this.page.networks.map((n) => n.factRefs.length)));
  }

  hasNodeRefs(): boolean {
    return (
      'NodeMemberMissing' === this.factName() ||
      'NodeInvalidSurveyDate' === this.factName() ||
      'IntegrityCheckFailed' === this.factName()
    );
  }

  hasOsmNodeRefs(): boolean {
    return 'NetworkExtraMemberNode' === this.factName();
  }

  hasOsmWayRefs(): boolean {
    return 'NetworkExtraMemberWay' === this.factName();
  }

  hasOsmRelationRefs(): boolean {
    return 'NetworkExtraMemberRelation' === this.factName();
  }

  hasRouteRefs(): boolean {
    return !(
      this.hasNodeRefs() ||
      this.hasOsmNodeRefs() ||
      this.hasOsmWayRefs() ||
      this.hasOsmRelationRefs()
    );
  }
}
