import {ChangeDetectionStrategy} from "@angular/core";
import {Component, OnDestroy, OnInit} from "@angular/core";
import {Subscriptions} from "../../../../util/Subscriptions";
import {ChangeFilterOptions} from "./change-filter-options";
import {ChangesService} from "./changes.service";

@Component({
  selector: "kpn-changes-sidebar",
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-sidebar>
      <kpn-change-filter [filterOptions]="filterOptions"></kpn-change-filter>
    </kpn-sidebar>
  `
})
export class ChangesSidebarComponent implements OnInit, OnDestroy {

  filterOptions: ChangeFilterOptions;
  private readonly subscriptions = new Subscriptions();

  constructor(private changesService: ChangesService) {
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.changesService.filterOptions.subscribe(filterOptions => this.filterOptions = filterOptions)
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

}
