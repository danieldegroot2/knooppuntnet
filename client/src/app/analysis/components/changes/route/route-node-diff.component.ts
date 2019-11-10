import {Component, Input} from "@angular/core";
import {List} from "immutable";
import {Ref} from "../../../../kpn/api/common/common/ref";

@Component({
  selector: "kpn-route-node-diff",
  template: `
    <span *ngIf="title === 'startNodes'" i18n="@@route-changes.node-diff.start-nodes">
        Start nodes
      </span>
    <span *ngIf="title === 'endNodes'" i18n="@@route-changes.node-diff.end-nodes">
        End nodes
      </span>
    <span *ngIf="title === 'startTentacleNodes'" i18n="@@route-changes.node-diff.start-tentacle-nodes">
        Start tentacle nodes
      </span>
    <span *ngIf="title === 'endTentacleNodes'" i18n="@@route-changes.node-diff.end-tentacle-nodes">
        End tentacle nodes
      </span>

    <span *ngIf="action === 'added'" i18n="@@route-changes.node-diff.added">added</span>
    <span *ngIf="action === 'removed'" i18n="@@route-changes.node-diff.removed">removed</span>:

    <div class="kpn-comma-list">
      <kpn-link-node
        *ngFor="let node of nodeRefs"
        [nodeId]="node.id"
        [nodeName]="node.name">
      </kpn-link-node>
    </div>
  `
})
export class RouteNodeDiffComponent {
  @Input() title: string; // startNodes | endNodes | startTentacleNodes | endTentacleNodes
  @Input() action: string; // added | removed
  @Input() nodeRefs: List<Ref>;
}
