import { NgIf } from '@angular/common';
import { AsyncPipe } from '@angular/common';
import { OnDestroy } from '@angular/core';
import { OnInit } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageComponent } from '@app/components/shared/page';
import { AnalysisSidebarComponent } from '@app/components/shared/sidebar';
import { Store } from '@ngrx/store';
import { RoutePageHeaderComponent } from '../components/route-page-header.component';
import { actionRouteMapPageDestroy } from '../store/route.actions';
import { actionRouteMapPageInit } from '../store/route.actions';
import { selectRouteNetworkType } from '../store/route.selectors';
import { selectRouteMapPage } from '../store/route.selectors';
import { selectRouteChangeCount } from '../store/route.selectors';
import { selectRouteName } from '../store/route.selectors';
import { selectRouteId } from '../store/route.selectors';
import { RouteMapComponent } from './route-map.component';

@Component({
  selector: 'kpn-route-changes-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page>
      <ul class="breadcrumb">
        <li><a routerLink="/" i18n="@@breadcrumb.home">Home</a></li>
        <li>
          <a routerLink="/analysis" i18n="@@breadcrumb.analysis">Analysis</a>
        </li>
        <li i18n="@@breadcrumb.route-map">Route map</li>
      </ul>

      <kpn-route-page-header
        pageName="map"
        [routeId]="routeId()"
        [routeName]="routeName()"
        [changeCount]="changeCount()"
        [networkType]="networkType()"
      />

      <div *ngIf="apiResponse() as response">
        <div
          *ngIf="!response.result"
          class="kpn-spacer-above"
          i18n="@@route.route-not-found"
        >
          Route not found
        </div>
        <div *ngIf="response.result">
          <kpn-route-map />
        </div>
      </div>
      <kpn-analysis-sidebar sidebar />
    </kpn-page>
  `,
  standalone: true,
  imports: [
    AnalysisSidebarComponent,
    AsyncPipe,
    NgIf,
    PageComponent,
    RouteMapComponent,
    RoutePageHeaderComponent,
    RouterLink,
  ],
})
export class RouteMapPageComponent implements OnInit, OnDestroy {
  readonly routeId = this.store.selectSignal(selectRouteId);
  readonly routeName = this.store.selectSignal(selectRouteName);
  readonly changeCount = this.store.selectSignal(selectRouteChangeCount);
  readonly apiResponse = this.store.selectSignal(selectRouteMapPage);
  readonly networkType = this.store.selectSignal(selectRouteNetworkType);

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(actionRouteMapPageInit());
  }

  ngOnDestroy(): void {
    this.store.dispatch(actionRouteMapPageDestroy());
  }
}
