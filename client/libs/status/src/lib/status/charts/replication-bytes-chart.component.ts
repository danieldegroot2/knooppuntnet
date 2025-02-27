import { ChangeDetectionStrategy } from '@angular/core';
import { Input } from '@angular/core';
import { Component } from '@angular/core';
import { BarChart } from '@api/common/status';
import { ActionBarChartComponent } from './action-bar-chart.component';

@Component({
  selector: 'kpn-replication-bytes-chart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <!-- English only-->
    <!-- eslint-disable @angular-eslint/template/i18n -->
    <h2>Replication bytes</h2>
    <div class="chart">
      <kpn-action-bar-chart
        [barChart]="barChart"
        [xAxisLabel]="xAxisLabel"
        yAxisLabel="Bytes downloaded"
      />
    </div>
  `,
  standalone: true,
  imports: [ActionBarChartComponent],
})
export class ReplicationBytesChartComponent {
  @Input() barChart: BarChart;
  @Input() xAxisLabel: string;
}
