import {Component, Input} from "@angular/core";
import {List} from "immutable";
import {NetworkChangeInfo} from "../../../kpn/api/common/changes/details/network-change-info";
import {KnownElements} from "../../../kpn/api/common/common/known-elements";
import {Ref} from "../../../kpn/api/common/common/ref";

@Component({
  selector: "kpn-cs-nc-orphan-nodes-old",
  template: `
    <div *ngIf="!refs().isEmpty()" class="kpn-detail kpn-line">
      <span i18n="@@change-set.network-diffs.orphan-nodes-resolved" class="kpn-label">
        Orphan nodes added to this network
      </span>
      <div class="kpn-comma-list">
        <kpn-link-node-ref
          *ngFor="let ref of refs()"
          [ref]="ref"
          [knownElements]="knownElements">
        </kpn-link-node-ref>
      </div>
      <kpn-icon-happy></kpn-icon-happy>
    </div>
  `
})
export class CsNcOrphanNodesOldComponent {

  @Input() networkChangeInfo: NetworkChangeInfo;
  @Input() knownElements: KnownElements;

  refs(): List<Ref> {
    return this.networkChangeInfo.orphanNodes.oldRefs;
  }

}
