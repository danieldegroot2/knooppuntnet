import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { Store } from '@ngrx/store';
import { actionPlannerPoisVisible } from '../../store/planner-actions';
import { selectPlannerPoisVisible } from '../../store/planner-selectors';
import { PoiMenuOptionComponent } from './poi-menu-option.component';

@Component({
  selector: 'kpn-poi-menu',
  // changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <mat-divider></mat-divider>

    <mat-checkbox
      (click)="$event.stopPropagation()"
      [checked]="visible()"
      (change)="visibleChanged($event)"
      class="pois"
      i18n="@@poi.menu.pois"
    >
      Points of interest
    </mat-checkbox>
    <div>
      <kpn-poi-menu-option
        groupName="hiking-biking"
        i18n="@@poi.group.hiking-biking"
      >
        Hiking/biking
      </kpn-poi-menu-option>
      <kpn-poi-menu-option groupName="landmarks" i18n="@@poi.group.landmarks">
        Landmarks
      </kpn-poi-menu-option>
      <kpn-poi-menu-option
        groupName="restaurants"
        i18n="@@poi.group.restaurants"
      >
        Restaurants
      </kpn-poi-menu-option>
      <kpn-poi-menu-option
        groupName="places-to-stay"
        i18n="@@poi.group.places-to-stay"
      >
        Places to stay
      </kpn-poi-menu-option>
      <kpn-poi-menu-option groupName="tourism" i18n="@@poi.group.tourism">
        Tourism
      </kpn-poi-menu-option>
      <kpn-poi-menu-option groupName="amenity" i18n="@@poi.group.amenity">
        Amenity
      </kpn-poi-menu-option>
      <kpn-poi-menu-option groupName="shops" i18n="@@poi.group.shops">
        Shops
      </kpn-poi-menu-option>
      <kpn-poi-menu-option groupName="foodshops" i18n="@@poi.group.foodshops">
        Foodshops
      </kpn-poi-menu-option>
      <kpn-poi-menu-option groupName="sports" i18n="@@poi.group.sports">
        Sports
      </kpn-poi-menu-option>
    </div>
  `,
  styles: [
    `
      mat-divider {
        margin-top: 5px;
        margin-bottom: 5px;
      }

      .pois {
        padding-right: 20px;
      }
    `,
  ],
  standalone: true,
  imports: [
    AsyncPipe,
    MatCheckboxModule,
    MatDividerModule,
    PoiMenuOptionComponent,
  ],
})
export class PoiMenuComponent {
  readonly visible = this.store.selectSignal(selectPlannerPoisVisible);

  constructor(private store: Store) {}

  visibleChanged(event: MatCheckboxChange): void {
    this.store.dispatch(actionPlannerPoisVisible({ visible: event.checked }));
  }
}
