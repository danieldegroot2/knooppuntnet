import {Component} from '@angular/core';

@Component({
  selector: 'kpn-planner-page',
  template: `
    <h1>
      Planner
    </h1>

    <kpn-icon-button
      routerLink="/planner/map/rcn"
      icon="rcn"
      text="Cycling">
    </kpn-icon-button>

    <kpn-icon-button
      routerLink="/planner/map/rwn"
      icon="rwn"
      text="Hiking">
    </kpn-icon-button>

    <kpn-icon-button
      routerLink="/planner/map/rhn"
      icon="rhn"
      text="Horse">
    </kpn-icon-button>

    <kpn-icon-button
      routerLink="/planner/map/rmn"
      icon="rmn"
      text="Motorboat">
    </kpn-icon-button>

    <kpn-icon-button
      routerLink="/planner/map/rpn"
      icon="rpn"
      text="Canoe">
    </kpn-icon-button>

    <kpn-icon-button
      routerLink="/planner/map/rin"
      icon="rin"
      text="Inline skating">
    </kpn-icon-button>
  `
})
export class PlannerPageComponent {

}
