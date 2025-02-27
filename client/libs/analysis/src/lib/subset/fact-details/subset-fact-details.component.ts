import { NgIf } from '@angular/common';
import { NgFor } from '@angular/common';
import { OnInit } from '@angular/core';
import { Input } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SubsetFactDetailsPage } from '@api/common/subset';
import { EditParameters } from '@app/analysis/components/edit';
import { EditService } from '@app/components/shared';
import { IconHappyComponent } from '@app/components/shared/icon';
import { ItemComponent } from '@app/components/shared/items';
import { ItemsComponent } from '@app/components/shared/items';
import { LinkNodeComponent } from '@app/components/shared/link';
import { LinkRouteComponent } from '@app/components/shared/link';
import { OsmLinkNodeComponent } from '@app/components/shared/link';
import { OsmLinkRelationComponent } from '@app/components/shared/link';
import { OsmLinkWayComponent } from '@app/components/shared/link';
import { Store } from '@ngrx/store';

@Component({
  selector: 'kpn-subset-fact-details',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="page.networks.length === 0" class="kpn-line">
      <span i18n="@@subset-facts.no-facts">No facts</span>
      <kpn-icon-happy />
    </div>
    <div *ngIf="page.networks.length > 0">
      <div class="kpn-space-separated kpn-label">
        <span>{{ refCount }}</span>
        <span *ngIf="hasNodeRefs()" i18n="@@subset-facts.node-refs"
          >{refCount, plural, one {node} other {nodes}}</span
        >
        <span *ngIf="hasRouteRefs()" i18n="@@subset-facts.route-refs"
          >{refCount, plural, one {route} other {routes}}</span
        >
        <span *ngIf="hasOsmNodeRefs()" i18n="@@subset-facts.osm-node-refs"
          >{refCount, plural, one {node} other {nodes}}</span
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
        <span *ngIf="factCount !== refCount" i18n="@@subset-facts.fact-count"
          >{factCount, plural, one {(1 fact)} other {({{
            factCount
          }}
          facts)}}</span
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
            <a
              rel="nofollow"
              (click)="edit()"
              title="Open in editor (like JOSM)"
              i18n-title="@@edit.link.title"
              i18n="@@edit.link"
              >edit</a
            >
          </div>
          <div class="kpn-comma-list fact-detail">
            <span *ngFor="let ref of networkFactRefs.factRefs">
              <kpn-link-node
                *ngIf="hasNodeRefs()"
                [nodeId]="ref.id"
                [nodeName]="ref.name"
              />
              <kpn-link-route
                *ngIf="hasRouteRefs()"
                [routeId]="ref.id"
                [routeName]="ref.name"
                [networkType]="page.subsetInfo.networkType"
              />
              <kpn-osm-link-node
                *ngIf="hasOsmNodeRefs()"
                [nodeId]="ref.id"
                [title]="ref.name"
              />
              <kpn-osm-link-way
                *ngIf="hasOsmWayRefs()"
                [wayId]="ref.id"
                [title]="ref.name"
              />
              <kpn-osm-link-relation
                *ngIf="hasOsmRelationRefs()"
                [relationId]="ref.id"
                [title]="ref.name"
              />
            </span>
          </div>
        </kpn-item>
      </kpn-items>
    </div>
  `,
  styleUrls: ['./_subset-fact-details-page.component.scss'],
  standalone: true,
  imports: [
    IconHappyComponent,
    ItemComponent,
    ItemsComponent,
    LinkNodeComponent,
    LinkRouteComponent,
    NgFor,
    NgIf,
    OsmLinkNodeComponent,
    OsmLinkRelationComponent,
    OsmLinkWayComponent,
    RouterLink,
  ],
})
export class SubsetFactDetailsComponent implements OnInit {
  @Input() page: SubsetFactDetailsPage;

  refCount = 0;
  factCount = 0;

  constructor(private editService: EditService, private store: Store) {}

  ngOnInit(): void {
    this.refCount = this.calculateRefCount();
    this.factCount = this.calculateFactCount();
  }

  factName(): string {
    return this.page.fact;
  }

  private calculateRefCount(): number {
    return new Set(
      this.page.networks.flatMap((n) => n.factRefs.map((r) => r.id))
    ).size;
  }

  private calculateFactCount(): number {
    return this.page.networks.flatMap((n) => n.factRefs).length;
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

  edit() {
    const elementIds = this.page.networks
      .flatMap((n) => n.factRefs)
      .map((ref) => ref.id);

    let editParameters: EditParameters = null;

    if (this.hasNodeRefs() || this.hasOsmNodeRefs()) {
      editParameters = {
        nodeIds: elementIds,
      };
    } else if (this.hasOsmWayRefs()) {
      editParameters = {
        wayIds: elementIds,
      };
    } else if (this.hasOsmRelationRefs() || this.hasRouteRefs()) {
      editParameters = {
        relationIds: elementIds,
        fullRelation: true,
      };
    }
    this.editService.edit(editParameters);
  }
}
