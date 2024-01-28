import { OnInit } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { input } from '@angular/core';
import { LocationNodeInfo } from '@api/common/location';
import { NetworkScope } from '@api/custom';
import { NetworkType } from '@api/custom';
import { IntegrityIndicatorData } from '@app/components/shared/indicator';
import { IntegrityIndicatorComponent } from '@app/components/shared/indicator';
import { LocationNodeFactIndicatorComponent } from './location-node-fact-indicator.component';

@Component({
  selector: 'kpn-location-node-analysis',
  changeDetection: ChangeDetectionStrategy.OnPush,

  template: `
    <div class="analysis">
      <kpn-location-node-fact-indicator [node]="node()" />
      <kpn-integrity-indicator [data]="integrityIndicatorData" />
    </div>
  `,
  styles: `
    .analysis {
      display: flex;
    }
  `,
  standalone: true,
  imports: [LocationNodeFactIndicatorComponent, IntegrityIndicatorComponent],
})
export class LocationNodeAnalysisComponent implements OnInit {
  networkType = input<NetworkType | undefined>();
  networkScope = input<NetworkScope | undefined>();
  node = input<LocationNodeInfo | undefined>();

  integrityIndicatorData: IntegrityIndicatorData;

  ngOnInit(): void {
    this.integrityIndicatorData = new IntegrityIndicatorData(
      this.networkType(),
      this.networkScope(),
      this.node().routeReferences.length,
      this.node().expectedRouteCount
    );
  }
}
