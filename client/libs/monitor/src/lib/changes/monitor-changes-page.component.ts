import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { RouterLink } from '@angular/router';
import { ErrorComponent } from '@app/components/shared/error';
import { PageComponent } from '@app/components/shared/page';
import { PaginatorComponent } from '@app/components/shared/paginator';
import { SidebarComponent } from '@app/components/shared/sidebar';
import { MonitorChangesComponent } from '../components/monitor-changes.component';
import { MonitorPageMenuComponent } from '../components/monitor-page-menu.component';
import { MonitorChangesPageService } from './monitor-changes-page.service';

@Component({
  selector: 'kpn-monitor-changes-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page *ngIf="service.state() as state">
      <ul class="breadcrumb">
        <li><a routerLink="/" i18n="@@breadcrumb.home">Home</a></li>
        <li>
          <a routerLink="/monitor" i18n="@@breadcrumb.monitor">Monitor</a>
        </li>
        <li i18n="@@breadcrumb.monitor.changes">Changes</li>
      </ul>

      <h1 i18n="@@monitor.changes.title">Monitor</h1>

      <kpn-monitor-page-menu pageName="changes" />
      <kpn-error />

      <div *ngIf="state.response as response">
        <p *ngIf="!response.result" i18n="@@monitor.changes.no-changes">
          No group changes
        </p>
        <div *ngIf="response.result as page" class="kpn-spacer-above">
          <mat-slide-toggle
            [checked]="service.impact()"
            (change)="impactChanged($event)"
            i18n="@@monitor.changes.impact"
            >Impact
          </mat-slide-toggle>

          <kpn-paginator
            [pageSize]="service.pageSize()"
            (pageSizeChange)="pageSizeChanged($event)"
            [pageIndex]="page.pageIndex"
            (pageIndexChange)="pageIndexChanged($event)"
            [length]="page.totalChangeCount"
            [showPageSizeSelection]="true"
          />

          <kpn-monitor-changes
            [pageSize]="page.pageSize"
            [pageIndex]="page.pageIndex"
            [changes]="page.changes"
          />
        </div>
      </div>
      <kpn-sidebar sidebar />
    </kpn-page>
  `,
  providers: [MonitorChangesPageService],
  standalone: true,
  imports: [
    ErrorComponent,
    MatSlideToggleModule,
    MonitorChangesComponent,
    MonitorPageMenuComponent,
    NgIf,
    PageComponent,
    PaginatorComponent,
    RouterLink,
    SidebarComponent,
  ],
})
export class MonitorChangesPageComponent {
  constructor(protected service: MonitorChangesPageService) {}

  impactChanged(event: MatSlideToggleChange) {
    this.service.impactChanged(event.checked);
  }

  pageSizeChanged(pageSize: number) {
    this.service.pageSizeChanged(pageSize);
  }

  pageIndexChanged(pageIndex: number) {
    window.scroll(0, 0);
    this.service.pageIndexChanged(pageIndex);
  }
}
