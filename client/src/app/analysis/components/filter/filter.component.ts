import {ChangeDetectionStrategy} from "@angular/core";
import {Component, Input} from "@angular/core";
import {FilterOptions} from "../../../kpn/filter/filter-options";

@Component({
  selector: "kpn-filter",
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="filter">
      <kpn-filter-title [filterOptions]="filterOptions"></kpn-filter-title>
      <div *ngFor="let group of filterOptions.groups">
        <kpn-filter-checkbox-group *ngIf="group.name === 'role'"></kpn-filter-checkbox-group>
        <kpn-filter-radio-group *ngIf="group.name !== 'role'" [group]="group"></kpn-filter-radio-group>
      </div>
    </div>
  `,
  styleUrls: ["./filter.scss"]
})
export class FilterComponent {
  @Input() filterOptions: FilterOptions;
}
