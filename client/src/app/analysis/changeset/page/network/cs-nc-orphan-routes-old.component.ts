import { OnInit } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component, Input } from '@angular/core';
import { NetworkChangeInfo } from '@api/common/changes/details/network-change-info';
import { KnownElements } from '@api/common/common/known-elements';
import { Ref } from '@api/common/common/ref';

@Component({
  selector: 'kpn-cs-nc-orphan-routes-old',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="refs.length > 0" class="kpn-detail">
      <span
        i18n="@@change-set.network-changes.orphan-routes-added"
        class="kpn-label"
      >
        Orphan routes added to this network</span
      >
      <div class="kpn-comma-list">
        <kpn-link-route-ref
          *ngFor="let ref of refs"
          [ref]="ref"
          [knownElements]="knownElements"
        >
        </kpn-link-route-ref>
      </div>
      <kpn-icon-investigate class="kpn-separate"></kpn-icon-investigate>
    </div>
  `,
})
export class CsNcOrphanRoutesOldComponent implements OnInit {
  @Input() networkChangeInfo: NetworkChangeInfo;
  @Input() knownElements: KnownElements;

  refs: Ref[];

  ngOnInit(): void {
    this.refs = this.networkChangeInfo.orphanRoutes.oldRefs;
  }
}
