import { NgFor } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { Check } from '@api/common';
import { LinkNodeComponent } from '@app/components/shared/link';

@Component({
  selector: 'kpn-network-fact-checks',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <table
      title="node integrity check failures"
      i18n-title="@@network-facts.checks-table.title"
      class="kpn-table"
    >
      <thead>
        <tr>
          <th class="nr"></th>
          <th i18n="@@network-facts.checks-table.node">Node</th>
          <th i18n="@@network-facts.checks-table.expected">Expected</th>
          <th i18n="@@network-facts.checks-table.actual">Actual</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let check of checks; let i = index">
          <td>
            <span class="kpn-thin">{{ i + 1 }}</span>
          </td>
          <td>
            <kpn-link-node
              [nodeId]="check.nodeId"
              [nodeName]="check.nodeName"
            />
          </td>
          <td>
            {{ check.expected }}
          </td>
          <td>
            {{ check.actual }}
          </td>
        </tr>
      </tbody>
    </table>
  `,
  styles: [
    `
      .nr {
        min-width: 2em;
      }
    `,
  ],
  standalone: true,
  imports: [NgFor, LinkNodeComponent],
})
export class NetworkFactChecksComponent {
  @Input() checks: Check[];
}
