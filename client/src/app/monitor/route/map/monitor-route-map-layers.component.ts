import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatRadioChange } from '@angular/material/radio';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import { selectFalse } from '../../../core/core.state';
import { AppState } from '../../../core/core.state';
import { actionMonitorRouteMapOsmRelationVisible } from '../../store/monitor.actions';
import { actionMonitorRouteMapDeviationsVisible } from '../../store/monitor.actions';
import { actionMonitorRouteMapMatchesVisible } from '../../store/monitor.actions';
import { actionMonitorRouteMapReferenceVisible } from '../../store/monitor.actions';
import { actionMonitorRouteMapMode } from '../../store/monitor.actions';
import {
  selectMonitorRouteMapOsmRelationEnabled,
  selectMonitorRouteMapPage,
} from '../../store/monitor.selectors';
import { selectMonitorRouteMapDeviationsEnabled } from '../../store/monitor.selectors';
import { selectMonitorRouteMapMatchesEnabled } from '../../store/monitor.selectors';
import { selectMonitorRouteMapReferenceEnabled } from '../../store/monitor.selectors';
import { selectMonitorRouteMapOsmRelationVisible } from '../../store/monitor.selectors';
import { selectMonitorRouteMapDeviationsVisible } from '../../store/monitor.selectors';
import { selectMonitorRouteMapMatchesVisible } from '../../store/monitor.selectors';
import { selectMonitorRouteMapReferenceVisible } from '../../store/monitor.selectors';
import { selectMonitorRouteMapMode } from '../../store/monitor.selectors';

@Component({
  selector: 'kpn-monitor-route-map-layers',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="mode$ | async as mode" class="map-layers">
      <ng-container *ngIf="referenceType$ | async as referenceType">
        <mat-checkbox
          [checked]="referenceVisible$ | async"
          [disabled]="referenceDisabled$ | async"
          (change)="gpxVisibleChanged($event)"
        >
          <div class="kpn-line">
            <kpn-legend-line color="blue"></kpn-legend-line>
            <span
              *ngIf="referenceType === 'gpx'"
              i18n="@@monitor.route.map-layers.reference-layer.gpx"
              >GPX trace</span
            >
            <span
              *ngIf="referenceType === 'osm'"
              i18n="@@monitor.route.map-layers.reference-layer.osm"
              >OSM reference</span
            >
          </div>
        </mat-checkbox>

        <mat-checkbox
          [checked]="matchesVisible$ | async"
          [disabled]="matchesDisabled$ | async"
          (change)="gpxOkVisibleChanged($event)"
        >
          <div class="kpn-line">
            <kpn-legend-line color="green"></kpn-legend-line>
            <span
              *ngIf="referenceType === 'gpx'"
              i18n="@@monitor.route.map-layers.gpx-same-as-osm"
            >
              GPX same as OSM
            </span>
            <span
              *ngIf="referenceType === 'osm'"
              i18n="@@monitor.route.map-layers.reference-same-as-osm"
            >
              Reference matches
            </span>
          </div>
        </mat-checkbox>

        <mat-checkbox
          [checked]="deviationsVisible$ | async"
          [disabled]="gpxDeviationsDisabled$ | async"
          (change)="gpxNokVisibleChanged($event)"
        >
          <div class="kpn-line">
            <kpn-legend-line color="red"></kpn-legend-line>
            <span
              *ngIf="referenceType === 'gpx'"
              i18n="@@monitor.route.map-layers.deviations.gpx"
            >
              GPX where OSM is deviating
            </span>
            <span
              *ngIf="referenceType === 'osm'"
              i18n="@@monitor.route.map-layers.deviations.osm"
            >
              Reference deviations
            </span>
          </div>
        </mat-checkbox>

        <mat-checkbox
          [checked]="osmRelationVisible$ | async"
          [disabled]="osmRelationDisabled$ | async"
          (change)="osmRelationVisibleChanged($event)"
        >
          <div class="kpn-line">
            <kpn-legend-line color="gold"></kpn-legend-line>
            <span i18n="@@monitor.route.map-layers.osm-relation">
              OSM relation
            </span>
          </div>
        </mat-checkbox>
      </ng-container>
    </div>
  `,
  styles: [
    `
      .map-layers {
        margin-top: 1em;
      }

      mat-radio-button {
        display: block;
        padding-bottom: 10px;
      }
    `,
  ],
})
export class MonitorRouteMapLayersComponent {
  readonly mode$ = this.store.select(selectMonitorRouteMapMode);
  readonly referenceType$ = this.store
    .select(selectMonitorRouteMapPage)
    .pipe(map((response) => response?.result?.reference.referenceType));

  readonly referenceVisible$ = this.store.select(
    selectMonitorRouteMapReferenceVisible
  );
  readonly matchesVisible$ = this.store.select(
    selectMonitorRouteMapMatchesVisible
  );
  readonly deviationsVisible$ = this.store.select(
    selectMonitorRouteMapDeviationsVisible
  );
  readonly osmRelationVisible$ = this.store.select(
    selectMonitorRouteMapOsmRelationVisible
  );
  readonly referenceDisabled$ = selectFalse(
    this.store,
    selectMonitorRouteMapReferenceEnabled
  );
  readonly matchesDisabled$ = selectFalse(
    this.store,
    selectMonitorRouteMapMatchesEnabled
  );
  readonly gpxDeviationsDisabled$ = selectFalse(
    this.store,
    selectMonitorRouteMapDeviationsEnabled
  );
  readonly osmRelationDisabled$ = selectFalse(
    this.store,
    selectMonitorRouteMapOsmRelationEnabled
  );

  constructor(private store: Store<AppState>) {}

  modeChanged(event: MatRadioChange): void {
    this.store.dispatch(actionMonitorRouteMapMode({ mode: event.value }));
  }

  gpxVisibleChanged(event: MatCheckboxChange): void {
    this.store.dispatch(
      actionMonitorRouteMapReferenceVisible({ visible: event.checked })
    );
  }

  gpxOkVisibleChanged(event: MatCheckboxChange): void {
    this.store.dispatch(
      actionMonitorRouteMapMatchesVisible({ visible: event.checked })
    );
  }

  gpxNokVisibleChanged(event: MatCheckboxChange): void {
    this.store.dispatch(
      actionMonitorRouteMapDeviationsVisible({ visible: event.checked })
    );
  }

  osmRelationVisibleChanged(event: MatCheckboxChange): void {
    this.store.dispatch(
      actionMonitorRouteMapOsmRelationVisible({ visible: event.checked })
    );
  }
}
