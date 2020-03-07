import {Component, Input} from "@angular/core";
import {NodeDiffsData} from "./node-diffs-data";

@Component({
  selector: "kpn-node-diffs-removed",
  template: `
    <div *ngIf="!refs().isEmpty()" class="kpn-level-2">
      <div class="kpn-line kpn-level-2-header">
        <span class="kpn-thick" i18n="@@node-diffs-removed.title">Removed network nodes</span>
        <span>({{refs().size}})</span>
        <kpn-icon-investigate></kpn-icon-investigate>
      </div>
      <div class="kpn-level-2-body">
        <div *ngFor="let nodeRef of refs()" class="kpn-level-3">
          <div class="kpn-line kpn-level-3-header">
            <kpn-link-node-ref [ref]="nodeRef" [knownElements]="data.knownElements"></kpn-link-node-ref>
          </div>
          <div *ngFor="let nodeChangeInfo of data.findNodeChangeInfo(nodeRef)" class="kpn-level-3-body">
            <kpn-meta-data [metaData]="nodeChangeInfo.before"></kpn-meta-data>
          </div>
        </div>
      </div>
    </div>
  `
})
export class NodeDiffsRemovedComponent {

  @Input() data: NodeDiffsData;

  refs() {
    return this.data.refDiffs.removed;
  }

}
