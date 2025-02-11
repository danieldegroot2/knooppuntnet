import { NgIf } from '@angular/common';
import { AsyncPipe } from '@angular/common';
import { OnDestroy } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { ErrorComponent } from '@app/components/shared/error';
import { PageComponent } from '@app/components/shared/page';
import { Store } from '@ngrx/store';
import { LocationPageHeaderComponent } from '../components/location-page-header.component';
import { LocationResponseComponent } from '../components/location-response.component';
import { actionLocationRoutesPageDestroy } from '../store/location.actions';
import { actionLocationRoutesPageInit } from '../store/location.actions';
import { selectLocationRoutesPage } from '../store/location.selectors';
import { LocationRoutesSidebarComponent } from './location-routes-sidebar.component';
import { LocationRoutesComponent } from './location-routes.component';

@Component({
  selector: 'kpn-location-routes-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page>
      <kpn-location-page-header
        pageName="routes"
        pageTitle="Routes"
        i18n-pageTitle="@@location-routes.title"
      />

      <kpn-error />

      <div *ngIf="apiResponse() as response" class="kpn-spacer-above">
        <kpn-location-response [response]="response">
          <kpn-location-routes [page]="response.result" />
        </kpn-location-response>
      </div>
      <kpn-location-routes-sidebar sidebar />
    </kpn-page>
  `,
  standalone: true,
  imports: [
    AsyncPipe,
    ErrorComponent,
    LocationPageHeaderComponent,
    LocationResponseComponent,
    LocationRoutesComponent,
    LocationRoutesSidebarComponent,
    NgIf,
    PageComponent,
  ],
})
export class LocationRoutesPageComponent implements OnInit, OnDestroy {
  readonly apiResponse = this.store.selectSignal(selectLocationRoutesPage);

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(actionLocationRoutesPageInit());
  }

  ngOnDestroy(): void {
    this.store.dispatch(actionLocationRoutesPageDestroy());
  }
}
