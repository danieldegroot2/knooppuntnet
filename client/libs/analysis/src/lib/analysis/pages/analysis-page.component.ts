import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IconButtonComponent } from '@app/components/shared/icon';
import { PageComponent } from '@app/components/shared/page';
import { PageHeaderComponent } from '@app/components/shared/page';
import { AnalysisSidebarComponent } from '../analysis-sidebar.component';

@Component({
  selector: 'kpn-analysis-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page>
      <ul class="breadcrumb">
        <li><a routerLink="/" i18n="@@breadcrumb.home">Home</a></li>
        <li i18n="@@breadcrumb.analysis">Analysis</li>
      </ul>

      <kpn-page-header i18n="@@analysis-page.title">Analysis</kpn-page-header>

      <div class="buttons">
        <kpn-icon-button
          routerLink="/analysis/overview"
          icon="overview"
          i18n-title="@@analysis-page.overview"
          title="Overview"
        />
        <kpn-icon-button
          routerLink="/analysis/changes"
          icon="changes"
          i18n-title="@@analysis-page.changes"
          title="Changes"
        />
      </div>

      <div class="buttons">
        <kpn-icon-button
          routerLink="/analysis/cycling"
          icon="cycling"
          i18n-title="@@network-type.cycling"
          title="Cycling"
        />
        <kpn-icon-button
          routerLink="/analysis/hiking"
          icon="hiking"
          i18n-title="@@network-type.hiking"
          title="Hiking"
        />
        <kpn-icon-button
          routerLink="/analysis/horse-riding"
          icon="horse-riding"
          i18n-title="@@network-type.horse-riding"
          title="Horse riding"
        />
        <kpn-icon-button
          routerLink="/analysis/motorboat"
          icon="motorboat"
          i18n-title="@@network-type.motorboat"
          title="Motorboat"
        />
        <kpn-icon-button
          routerLink="/analysis/canoe"
          icon="canoe"
          i18n-title="@@network-type.canoe"
          title="Canoe"
        />
        <kpn-icon-button
          routerLink="/analysis/inline-skating"
          icon="inline-skating"
          i18n-title="@@network-type.inline-skating"
          title="Inline skating"
        />
      </div>
      <kpn-analysis-sidebar sidebar />
    </kpn-page>
  `,
  styles: [
    `
      .buttons {
        display: flex;
        flex-wrap: wrap;
      }
    `,
  ],
  standalone: true,
  imports: [
    AnalysisSidebarComponent,
    IconButtonComponent,
    PageComponent,
    PageHeaderComponent,
    RouterLink,
  ],
})
export class AnalysisPageComponent {}
