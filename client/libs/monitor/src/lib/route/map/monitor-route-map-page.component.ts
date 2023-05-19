import { NgIf } from '@angular/common';
import { computed } from '@angular/core';
import { OnInit } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { MonitorRouteMapPage } from '@api/common/monitor';
import { NavService } from '@app/components/shared';
import { MonitorRoutePageHeaderComponent } from '../components/monitor-route-page-header.component';
import { MonitorRouteMapPageService } from './monitor-route-map-page.service';
import { MonitorRouteMapStateService } from './monitor-route-map-state.service';
import { MonitorRouteMapComponent } from './monitor-route-map.component';
import { MonitorRouteMapService } from './monitor-route-map.service';

@Component({
  selector: 'kpn-monitor-route-map-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-monitor-route-page-header
      pageName="map"
      [groupName]="service.groupName()"
      [routeName]="service.routeName()"
      [routeDescription]="service.routeDescription()"
      [subRelations]="subRelations()"
      [previous]="previous()"
      [next]="next()"
      (selectSubRelation)="service.selectSubRelation($event)"
    />
    <div *ngIf="stateService.page() !== null">
      <div
        *ngIf="canDisplayMap(stateService.page()); then map; else noMap"
      ></div>
      <ng-template #noMap>
        <p i18n="@@monitor.route.map.no-map">No map</p>
      </ng-template>
      <ng-template #map>
        <kpn-monitor-route-map />
      </ng-template>
    </div>
  `,
  providers: [MonitorRouteMapPageService, MonitorRouteMapService, NavService],
  standalone: true,
  imports: [MonitorRouteMapComponent, MonitorRoutePageHeaderComponent, NgIf],
})
export class MonitorRouteMapPageComponent implements OnInit {
  readonly subRelations = computed(() => {
    return this.stateService.page()?.subRelations ?? [];
  });
  readonly previous = computed(() => {
    return this.stateService.page()?.previousSubRelation;
  });
  readonly next = computed(() => {
    return this.stateService.page()?.nextSubRelation;
  });

  constructor(
    protected service: MonitorRouteMapPageService,
    protected stateService: MonitorRouteMapStateService
  ) {}

  ngOnInit(): void {
    this.service.init();
  }

  canDisplayMap(page: MonitorRouteMapPage): boolean {
    return (
      page &&
      page.bounds &&
      (page.bounds.minLat !== 0 ||
        page.bounds.minLon !== 0 ||
        page.bounds.maxLat !== 0 ||
        page.bounds.maxLon !== 0)
    );
  }
}
