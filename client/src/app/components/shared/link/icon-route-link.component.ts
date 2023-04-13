import { ChangeDetectionStrategy } from '@angular/core';
import { Component, Input } from '@angular/core';
import { Reference } from '@api/common/common';

@Component({
  selector: 'kpn-icon-route-link',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-icon-link
      [reference]="reference"
      [mixedNetworkScopes]="mixedNetworkScopes"
      [url]="'/analysis/route/' + reference.id"
    />
  `,
})
export class IconRouteLinkComponent {
  @Input() reference: Reference;
  @Input() mixedNetworkScopes: boolean;
}
