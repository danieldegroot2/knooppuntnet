import { ChangeDetectionStrategy } from '@angular/core';
import { Component, Input } from '@angular/core';
import { Ref } from '@api/common/common/ref';

@Component({
  selector: 'kpn-network-fact-nodes',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span *ngIf="nodes.length == 1" class="title" i18n="@@network-facts.node">
      Node:
    </span>
    <span *ngIf="nodes.length > 1" class="title" i18n="@@network-facts.nodes">
      Nodes:
    </span>
    <div class="kpn-comma-list">
      <kpn-link-node
        *ngFor="let node of nodes"
        [nodeId]="node.id"
        [nodeName]="node.name"
      >
      </kpn-link-node>
    </div>
  `,
})
export class NetworkFactNodesComponent {
  @Input() nodes: Ref[];
}
