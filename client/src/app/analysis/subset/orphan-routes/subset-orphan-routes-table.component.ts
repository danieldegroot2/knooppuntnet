import {Component, Input, OnInit, ViewChild} from "@angular/core";
import {MatPaginator, MatTableDataSource} from "@angular/material";
import {List} from "immutable";
import {BehaviorSubject} from "rxjs";
import {RouteSummary} from "../../../kpn/api/common/route-summary";
import {TimeInfo} from "../../../kpn/api/common/time-info";
import {SubsetOrphanRouteFilter} from "./subset-orphan-route-filter";
import {SubsetOrphanRouteFilterCriteria} from "./subset-orphan-route-filter-criteria";
import {SubsetOrphanRoutesService} from "./subset-orphan-routes.service";

@Component({
  selector: "kpn-subset-orphan-routes-table",
  template: `
    <mat-paginator
      #paginator
      [length]="dataSource.data.length"
      [pageIndex]="0"
      [pageSize]="50"
      [pageSizeOptions]="[25, 50, 100, 250, 1000]">
    </mat-paginator>

    <mat-divider></mat-divider>
    <table mat-table [dataSource]="dataSource" class="kpn-columns-table">

      <ng-container matColumnDef="rowNumber">
        <td mat-cell *matCellDef="let route; let i = index">
          {{i + 1}}
        </td>
      </ng-container>

      <ng-container matColumnDef="route">
        <td mat-cell *matCellDef="let route">
          <kpn-subset-orphan-route [route]="route"></kpn-subset-orphan-route>
        </td>
      </ng-container>

      <tr mat-row *matRowDef="let route; columns: displayedColumns;"></tr>

    </table>
  `,
  styles: [`

    table {
      width: 100%;
    }

    .mat-column-rowNumber {
      width: 50px;
      vertical-align: top;
      padding-top: 15px;
    }

    td.mat-cell:first-of-type {
      padding-left: 10px;
    }

  `]
})
export class SubsetOrphanRoutesTableComponent implements OnInit {

  @Input() timeInfo: TimeInfo;
  @Input() orphanRoutes: List<RouteSummary> = List();

  @ViewChild(MatPaginator) paginator: MatPaginator;

  dataSource: MatTableDataSource<RouteSummary>;

  displayedColumns = ["rowNumber", "route"];

  private readonly filterCriteria: BehaviorSubject<SubsetOrphanRouteFilterCriteria> = new BehaviorSubject(new SubsetOrphanRouteFilterCriteria());

  constructor(private subsetOrphanRoutesService: SubsetOrphanRoutesService) {
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource();
    this.dataSource.paginator = this.paginator;
    this.filterCriteria.subscribe(criteria => {
      const filter = new SubsetOrphanRouteFilter(this.timeInfo, criteria, this.filterCriteria);
      this.dataSource.data = filter.filter(this.orphanRoutes).toArray();
      this.subsetOrphanRoutesService.filterOptions.next(filter.filterOptions(this.orphanRoutes));
    });
  }
}
