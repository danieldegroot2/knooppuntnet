import { inject } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { PageComponent } from '@app/components/shared/page';
import { RouterService } from '../../../shared/services/router.service';
import { AnalysisSidebarComponent } from '../../analysis/analysis-sidebar.component';
import { NetworkPageHeaderComponent } from '../components/network-page-header.component';
import { NetworkDetailsComponent } from './components/network-details.component';
import { NetworkDetailsStore } from './network-details.store';

@Component({
  selector: 'kpn-network-details-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page>
      <kpn-network-page-header
        pageName="details"
        pageTitle="Details"
        i18n-pageTitle="@@network-details.title"
      />

      @if (store.response(); as response) {
        <div class="kpn-spacer-above">
          @if (!response.result) {
            <p i18n="@@network-page.network-not-found">Network not found</p>
          } @else {
            <kpn-network-details [response]="response" />
          }
        </div>
      }
      <kpn-analysis-sidebar sidebar />
    </kpn-page>
  `,
  providers: [NetworkDetailsStore, RouterService],
  standalone: true,
  imports: [
    AnalysisSidebarComponent,
    NetworkDetailsComponent,
    NetworkPageHeaderComponent,
    PageComponent,
  ],
})
export class NetworkDetailsPageComponent {
  protected readonly store = inject(NetworkDetailsStore);
}
