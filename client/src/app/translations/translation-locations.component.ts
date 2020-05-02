import {ChangeDetectionStrategy} from "@angular/core";
import {Component, Input} from "@angular/core";
import {List} from "immutable";
import {TranslationLocation} from "./domain/translation-location";

/* tslint:disable:template-i18n */
@Component({
  selector: "kpn-translation-locations",
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="locations">
      <mat-checkbox [checked]="showLocations" (change)="toggleShowLocations()">
        Show source code usage locations ({{locations.size}})
      </mat-checkbox>
      <div *ngIf="showLocations">
        <div *ngFor="let location of locations">
          <kpn-translation-location [location]="location"></kpn-translation-location>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .locations {
      margin-top: 10px;
    }
  `]
})
export class TranslationLocationsComponent {

  @Input() locations: List<TranslationLocation>;

  showLocations = false;

  toggleShowLocations(): void {
    this.showLocations = !this.showLocations;
  }

}
