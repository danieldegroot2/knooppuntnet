import { NgIf } from '@angular/common';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { actionLocationPoisPageInit } from '../store/poi.actions';
import { selectLocationPoisPage } from '../store/poi.selectors';
import { PoiLocationPoiTableComponent } from './poi-location-poi-table.component';

@Component({
  selector: 'kpn-poi-location-pois-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="apiResponse() as response">
      <div *ngIf="response.result as page">
        <kpn-poi-location-poi-table
          [pois]="page.pois"
          [poiCount]="page.poiCount"
        />
      </div>
    </div>
  `,
  standalone: true,
  imports: [NgIf, PoiLocationPoiTableComponent, AsyncPipe],
})
export class PoiLocationPoisPageComponent implements OnInit {
  readonly apiResponse = this.store.selectSignal(selectLocationPoisPage);

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(actionLocationPoisPageInit());
  }
}
