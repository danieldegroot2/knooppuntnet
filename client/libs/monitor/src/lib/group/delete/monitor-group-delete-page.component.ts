import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { NavService } from '@app/components/shared';
import { PageComponent } from '@app/components/shared/page';
import { SidebarComponent } from '@app/components/shared/sidebar';
import { MonitorGroupBreadcrumbComponent } from '../components/monitor-group-breadcrumb.component';
import { MonitorGroupDeletePageService } from './monitor-group-delete-page.service';

@Component({
  selector: 'kpn-monitor-group-delete-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page *ngIf="service.state() as state">
      <kpn-monitor-group-breadcrumb />

      <h1 i18n="@@monitor.group.delete.title">Monitor - delete group</h1>

      <div *ngIf="state.response as response" class="kpn-form">
        <div *ngIf="!response.result">
          <p i18n="@@monitor.group.delete.group-not-found">Group not found</p>
        </div>
        <div *ngIf="response.result as page" class="kpn-form">
          <p>
            <span class="kpn-label" i18n="@@monitor.group.delete.name">
              Name
            </span>
            {{ page.groupName }}
          </p>

          <p>
            <span class="kpn-label" i18n="@@monitor.group.delete.description">
              Description
            </span>
            {{ page.groupDescription }}
          </p>

          <div *ngIf="page.routes.length as routeCount">
            <div *ngIf="routeCount > 0" class="kpn-line">
              <mat-icon svgIcon="warning" />
              <span i18n="@@monitor.group.delete.warning">
                The information of all routes ({{ routeCount }} route(s)) in the
                group will also be deleted!
              </span>
            </div>
          </div>

          <div class="kpn-form-buttons">
            <button mat-stroked-button (click)="service.delete(page.groupId)">
              <span class="kpn-warning" i18n="@@monitor.group.delete.action">
                Delete group
              </span>
            </button>
            <a routerLink="/monitor" i18n="@@action.cancel">Cancel</a>
          </div>
        </div>
      </div>
      <kpn-sidebar sidebar />
    </kpn-page>
  `,
  providers: [MonitorGroupDeletePageService, NavService],
  standalone: true,
  imports: [
    MatButtonModule,
    MatIconModule,
    MonitorGroupBreadcrumbComponent,
    NgIf,
    PageComponent,
    RouterLink,
    SidebarComponent,
  ],
})
export class MonitorGroupDeletePageComponent {
  constructor(protected service: MonitorGroupDeletePageService) {}
}
