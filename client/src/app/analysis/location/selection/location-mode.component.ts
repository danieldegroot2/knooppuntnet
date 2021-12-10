import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { MatRadioChange } from '@angular/material/radio';
import { LocationModeService } from './location-mode.service';

@Component({
  selector: 'kpn-location-mode',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="sidebar-section">
      <div class="sidebar-section-title">Location</div>

      <mat-radio-group [value]="mode()" (change)="modeChanged($event)">
        <mat-radio-button
          value="name"
          title="Name"
          i18n="@@analysis.location.search-by-name"
        >
          Search by name
        </mat-radio-button>
        <mat-radio-button
          value="tree"
          title="Tree"
          i18n="@@analysis.location.select-from-tree"
        >
          Select from tree
        </mat-radio-button>
      </mat-radio-group>
    </div>
  `,
  styleUrls: ['../../../components/shared/sidebar/sidebar.scss'],
})
export class LocationModeComponent {
  constructor(private locationModeService: LocationModeService) {}

  mode() {
    return this.locationModeService.currentMode();
  }

  modeChanged(event: MatRadioChange) {
    this.locationModeService.setMode(event.value);
  }
}
