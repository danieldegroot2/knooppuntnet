import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { PoiConfigComponent } from './poi-config.component';
import { PoiGroupComponent } from './poi-group.component';

@Component({
  selector: 'kpn-poi-group-hiking-biking',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-poi-group
      name="hiking-biking"
      title="Hiking/biking"
      i18n-title="@@poi.group.hiking-biking"
    >
      <kpn-poi-config poiId="ebike-charging" />
      <kpn-poi-config poiId="bicycle" />
      <kpn-poi-config poiId="bicycle-rental" />
      <kpn-poi-config poiId="bicycle-rental-2" />
      <kpn-poi-config poiId="bicycle-parking" />
      <kpn-poi-config poiId="information" />
      <kpn-poi-config poiId="bench" />
      <kpn-poi-config poiId="picnic" />
      <kpn-poi-config poiId="toilets" />
      <kpn-poi-config poiId="drinking-water" />
      <kpn-poi-config poiId="themepark" />
      <kpn-poi-config poiId="viewpoint" />
      <kpn-poi-config poiId="attraction" />
      <kpn-poi-config poiId="defibrillator" />
    </kpn-poi-group>
  `,
  standalone: true,
  imports: [PoiGroupComponent, PoiConfigComponent],
})
export class PoiGroupHikingBikingComponent {}
