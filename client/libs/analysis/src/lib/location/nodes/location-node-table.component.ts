import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { OnChanges } from '@angular/core';
import { SimpleChanges } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { OnInit } from '@angular/core';
import { ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatTableModule } from '@angular/material/table';
import { TimeInfo } from '@api/common';
import { LocationNodeInfo } from '@api/common/location';
import { NetworkScope } from '@api/custom';
import { EditAndPaginatorComponent } from '@app/analysis/components/edit';
import { EditService } from '@app/components/shared';
import { PageWidthService } from '@app/components/shared';
import { DayComponent } from '@app/components/shared/day';
import { DayPipe } from '@app/components/shared/format';
import { OsmLinkNodeComponent } from '@app/components/shared/link';
import { JosmNodeComponent } from '@app/components/shared/link';
import { LinkNodeComponent } from '@app/components/shared/link';
import { PaginatorComponent } from '@app/components/shared/paginator';
import { selectPreferencesPageSize } from '@app/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EditParameters } from '../../components/edit';
import { actionLocationNodesPageSize } from '../store/location.actions';
import { actionLocationNodesPageIndex } from '../store/location.actions';
import { selectLocationNetworkType } from '../store/location.selectors';
import { selectLocationNodesPageIndex } from '../store/location.selectors';
import { LocationNodeAnalysisComponent } from './location-node-analysis.component';
import { LocationNodeRoutesComponent } from './location-node-routes.component';

@Component({
  selector: 'kpn-location-node-table',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-edit-and-paginator
      (edit)="edit()"
      editLinkTitle="Load the nodes in this page in the editor (like JOSM)"
      i18n-editLinkTitle="@@location-nodes.edit.title"
      [pageIndex]="pageIndex()"
      (pageIndexChange)="onPageIndexChange($event)"
      [pageSize]="pageSize()"
      (pageSizeChange)="onPageSizeChange($event)"
      [length]="nodeCount"
      [showFirstLastButtons]="false"
      [showPageSizeSelection]="true"
    />

    <table mat-table [dataSource]="dataSource">
      <ng-container matColumnDef="nr">
        <th mat-header-cell *matHeaderCellDef i18n="@@location-nodes.table.nr">
          Nr
        </th>
        <td mat-cell *matCellDef="let node">{{ node.rowIndex + 1 }}</td>
      </ng-container>

      <ng-container matColumnDef="analysis">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.analysis"
        >
          Analysis
        </th>
        <td mat-cell *matCellDef="let node">
          <kpn-location-node-analysis
            [node]="node"
            [networkType]="networkType()"
            [networkScope]="networkScope"
          />
        </td>
      </ng-container>

      <ng-container matColumnDef="node">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.node"
        >
          Node
        </th>
        <td mat-cell *matCellDef="let node">
          <kpn-link-node [nodeId]="node.id" [nodeName]="node.name" />
        </td>
      </ng-container>

      <ng-container matColumnDef="name">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.name"
        >
          Name
        </th>
        <td mat-cell *matCellDef="let node">
          {{ node.longName }}
        </td>
      </ng-container>

      <ng-container matColumnDef="expectedRouteCount">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.expected-route-count"
        >
          Expected
        </th>
        <td mat-cell *matCellDef="let node">
          {{ node.expectedRouteCount }}
        </td>
      </ng-container>

      <ng-container matColumnDef="routes">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.routes"
        >
          Routes
        </th>
        <td mat-cell *matCellDef="let node">
          <kpn-location-node-routes [node]="node" />
        </td>
      </ng-container>

      <ng-container matColumnDef="last-survey">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.last-survey"
        >
          Survey
        </th>
        <td mat-cell *matCellDef="let node">
          {{ node.lastSurvey | day }}
        </td>
      </ng-container>

      <ng-container matColumnDef="lastEdit">
        <th
          mat-header-cell
          *matHeaderCellDef
          i18n="@@location-nodes.table.last-edit"
        >
          Last edit
        </th>
        <td mat-cell *matCellDef="let node" class="kpn-separated">
          <kpn-day [timestamp]="node.lastUpdated" />
          <kpn-josm-node [nodeId]="node.id" />
          <kpn-osm-link-node [nodeId]="node.id" />
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns$ | async"></tr>
      <tr
        mat-row
        *matRowDef="let node; columns: displayedColumns$ | async"
      ></tr>
    </table>

    <kpn-paginator
      [pageIndex]="pageIndex()"
      (pageIndexChange)="onPageIndexChange($event)"
      [pageSize]="pageSize()"
      (pageSizeChange)="onPageSizeChange($event)"
      [length]="nodeCount"
    />
  `,
  styles: [
    `
      .mat-column-nr {
        flex: 0 0 4em;
      }
    `,
  ],
  standalone: true,
  imports: [
    AsyncPipe,
    DayComponent,
    DayPipe,
    EditAndPaginatorComponent,
    JosmNodeComponent,
    LinkNodeComponent,
    LocationNodeAnalysisComponent,
    LocationNodeRoutesComponent,
    MatTableModule,
    OsmLinkNodeComponent,
    PaginatorComponent,
  ],
})
export class LocationNodeTableComponent implements OnInit, OnChanges {
  // TODO !!!
  networkScope: NetworkScope = NetworkScope.regional;

  @Input() timeInfo: TimeInfo;
  @Input() nodes: LocationNodeInfo[];
  @Input() nodeCount: number;

  @ViewChild(PaginatorComponent, { static: true })
  paginator: PaginatorComponent;

  readonly pageSize = this.store.selectSignal(selectPreferencesPageSize);
  readonly pageIndex = this.store.selectSignal(selectLocationNodesPageIndex);
  readonly networkType = this.store.selectSignal(selectLocationNetworkType);

  dataSource: MatTableDataSource<LocationNodeInfo>;
  displayedColumns$: Observable<Array<string>>;

  constructor(
    private pageWidthService: PageWidthService,
    private editService: EditService,
    private store: Store
  ) {
    this.dataSource = new MatTableDataSource();
    this.displayedColumns$ = pageWidthService.current$.pipe(
      map(() => this.displayedColumns())
    );
  }

  ngOnInit(): void {
    this.dataSource.data = this.nodes;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['nodes']) {
      this.dataSource.data = this.nodes;
    }
  }

  onPageSizeChange(pageSize: number) {
    this.store.dispatch(actionLocationNodesPageSize({ pageSize }));
  }

  onPageIndexChange(pageIndex: number) {
    window.scroll(0, 0);
    this.store.dispatch(actionLocationNodesPageIndex({ pageIndex }));
  }

  edit(): void {
    const editParameters: EditParameters = {
      nodeIds: this.nodes.map((node) => node.id),
    };
    this.editService.edit(editParameters);
  }

  private displayedColumns() {
    if (this.pageWidthService.isVeryLarge()) {
      return [
        'nr',
        'analysis',
        'node',
        'name',
        'expectedRouteCount',
        'routes',
        'last-survey',
        'lastEdit',
      ];
    }

    if (this.pageWidthService.isLarge()) {
      return ['nr', 'analysis', 'node', 'name', 'expectedRouteCount', 'routes'];
    }

    return ['nr', 'node', 'expectedRouteCount', 'routes'];
  }
}
