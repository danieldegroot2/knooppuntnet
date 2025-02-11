import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MonitorGroupPage } from '@api/common/monitor';
import { NavService } from '@app/components/shared';
import { PageComponent } from '@app/components/shared/page';
import { SidebarComponent } from '@app/components/shared/sidebar';
import { MonitorAdminToggleComponent } from '../../components/monitor-admin-toggle.component';
import { MonitorGroupPageMenuComponent } from '../components/monitor-group-page-menu.component';
import { MonitorGroupPageService } from './monitor-group-page.service';
import { MonitorGroupRouteTableComponent } from './monitor-group-route-table.component';

@Component({
  selector: 'kpn-monitor-group-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page *ngIf="service.state() as state">
      <ul class="breadcrumb">
        <li><a routerLink="/" i18n="@@breadcrumb.home">Home</a></li>
        <li>
          <a routerLink="/monitor" i18n="@@breadcrumb.monitor">Monitor</a>
        </li>
        <li i18n="@@breadcrumb.monitor.group">Group</li>
      </ul>

      <h1 class="title" *ngIf="state.groupName">
        <span class="kpn-label">{{ state.groupName }}</span>
        <span>{{ state.groupDescription }}</span>
      </h1>

      <kpn-monitor-group-page-menu
        pageName="routes"
        [groupName]="state.groupName"
      />

      <kpn-monitor-admin-toggle />

      <div *ngIf="state.response as response" class="kpn-form">
        <ng-container *ngIf="response.result as page">
          <div *ngIf="page.routes.length > 0; else noRoutes">
            <kpn-monitor-group-route-table
              [admin]="service.admin()"
              [groupName]="page.groupName"
              [routes]="page.routes"
            />
          </div>
          <ng-template #noRoutes>
            <div id="no-routes" i18n="@@monitor.group.no-routes">
              No routes in group
            </div>
          </ng-template>
          <div *ngIf="service.admin()" class="kpn-form-buttons">
            <button
              mat-stroked-button
              id="add-route"
              [routerLink]="addRouteLink(page)"
              type="button"
              i18n="@@monitor.group.action.add-route"
            >
              Add route
            </button>
          </div>
        </ng-container>
      </div>
      <kpn-sidebar sidebar />
    </kpn-page>
  `,
  providers: [NavService, MonitorGroupPageService],
  standalone: true,
  imports: [
    MatButtonModule,
    MonitorAdminToggleComponent,
    MonitorGroupPageMenuComponent,
    MonitorGroupRouteTableComponent,
    NgIf,
    PageComponent,
    RouterLink,
    SidebarComponent,
  ],
})
export class MonitorGroupPageComponent {
  constructor(protected service: MonitorGroupPageService) {}

  addRouteLink(page: MonitorGroupPage): string {
    return `/monitor/admin/groups/${page.groupName}/routes/add`;
  }
}
