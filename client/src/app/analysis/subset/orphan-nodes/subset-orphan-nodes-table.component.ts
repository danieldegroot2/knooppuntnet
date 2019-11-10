import {Component, Input, OnInit, ViewChild} from "@angular/core";
import {MatPaginator, MatTableDataSource} from "@angular/material";
import {List} from "immutable";
import {BehaviorSubject} from "rxjs";
import {NodeInfo} from "../../../kpn/api/common/node-info";
import {TimeInfo} from "../../../kpn/api/common/time-info";
import {SubsetOrphanNodeFilter} from "./subset-orphan-node-filter";
import {SubsetOrphanNodeFilterCriteria} from "./subset-orphan-node-filter-criteria";
import {SubsetOrphanNodesService} from "./subset-orphan-nodes.service";

@Component({
  selector: "kpn-subset-orphan-nodes-table",
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

      <ng-container matColumnDef="node">
        <td mat-cell *matCellDef="let node">
          <kpn-subset-orphan-node [node]="node"></kpn-subset-orphan-node>
        </td>
      </ng-container>

      <tr mat-row *matRowDef="let node; columns: displayedColumns;"></tr>

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
export class SubsetOrphanNodesTableComponent implements OnInit {

  @Input() timeInfo: TimeInfo;
  @Input() nodes: List<NodeInfo>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  dataSource: MatTableDataSource<NodeInfo>;

  displayedColumns = ["rowNumber", "node"];

  private readonly filterCriteria: BehaviorSubject<SubsetOrphanNodeFilterCriteria> = new BehaviorSubject(new SubsetOrphanNodeFilterCriteria());

  constructor(private subsetOrphanNodesService: SubsetOrphanNodesService) {
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource();
    this.filterCriteria.subscribe(criteria => {
      const filter = new SubsetOrphanNodeFilter(this.timeInfo, criteria, this.filterCriteria);
      this.dataSource.data = filter.filter(this.nodes).toArray();
      this.subsetOrphanNodesService.filterOptions.next(filter.filterOptions(this.nodes));
    });
  }
}
