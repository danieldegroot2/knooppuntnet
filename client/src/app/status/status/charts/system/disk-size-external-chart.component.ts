import { ChangeDetectionStrategy } from '@angular/core';
import { Input } from '@angular/core';
import { Component } from '@angular/core';
import { BarChart } from '@api/common/status';

@Component({
  selector: 'kpn-disk-size-external-chart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <!-- English only-->
    <!-- eslint-disable @angular-eslint/template/i18n -->
    <h2>Disk size external</h2>
    <div class="chart">
      <kpn-action-bar-chart
        [barChart]="barChart"
        [xAxisLabel]="xAxisLabel"
        yAxisLabel="TODO bytes?"
      />
    </div>
  `,
})
export class DiskSizeExternalChartComponent {
  @Input() barChart: BarChart;
  @Input() xAxisLabel: string;
}
