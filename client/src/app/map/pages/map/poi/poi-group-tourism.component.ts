import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';

@Component({
  selector: 'kpn-poi-group-tourism',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-poi-group
      name="tourism"
      title="Tourism"
      i18n-title="@@poi.group.tourism"
    >
      <kpn-poi-config poiId="arts-centre"/>
      <kpn-poi-config poiId="artwork"/>
      <kpn-poi-config poiId="casino"/>
      <kpn-poi-config poiId="gallery"/>
      <kpn-poi-config poiId="monumental-tree"/>
      <kpn-poi-config poiId="museum"/>
      <kpn-poi-config poiId="vineyard"/>
      <kpn-poi-config poiId="tourism"/>
    </kpn-poi-group>
  `,
})
export class PoiGroupTourismComponent {}
