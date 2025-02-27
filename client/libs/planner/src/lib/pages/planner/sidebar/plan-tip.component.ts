import { NgIf } from '@angular/common';
import { AsyncPipe } from '@angular/common';
import { OnInit } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { ZoomLevel } from '@app/components/ol/domain';
import { MapZoomService } from '@app/components/ol/services';
import { I18nService } from '@app/i18n';
import { Observable } from 'rxjs';
import { combineLatest } from 'rxjs';
import { delay } from 'rxjs/operators';
import { map } from 'rxjs/operators';
import { Plan } from '../../../domain/plan/plan';
import { PlanPhase } from '../../../domain/plan/plan-phase';
import { PlannerService } from '../../../planner.service';

@Component({
  selector: 'kpn-plan-tip',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="planPhase$ | async as planPhase" class="tip">
      <p
        *ngIf="planPhaseEnum.zoomInClickStartNode === planPhase"
        i18n="@@planner-tip.zoom-in-click-start-node"
      >
        Zoom in or use magnifying glass to find the startnode of your route.
      </p>
      <p
        *ngIf="planPhaseEnum.clickStartNode === planPhase"
        i18n="@@planner-tip.click-start-node"
      >
        Use zoom, pan or use magnifying glass to find and click the startnode of
        your route.
      </p>
      <p
        *ngIf="planPhaseEnum.zoomInClickEndNode === planPhase"
        i18n="@@planner-tip.zoom-in-click-end-node"
      >
        Zoom in and click the endnode of your route.
      </p>
      <p
        *ngIf="planPhaseEnum.clickEndNode === planPhase"
        i18n="@@planner-tip.click-end-node"
      >
        Click the endnode of your route.
      </p>
      <p *ngIf="planPhaseEnum.extendRoute === planPhase">
        <ng-container i18n="@@planner-tip.extend-route">
          Extend or adapt your route. Output when satisfied.
        </ng-container>
        <a
          id="read-more"
          [href]="more()"
          i18n="@@planner-tip.read-more"
          target="knooppuntnet-documentation"
          >read more</a
        >
      </p>
    </div>
  `,
  styles: [
    `
      .tip {
        height: 70px;
        font-style: italic;
      }

      #read-more {
        padding-left: 10px;
      }

      #read-more::before {
        content: '(';
        color: black;
      }

      #read-more::after {
        content: ')';
        color: black;
      }
    `,
  ],
  standalone: true,
  imports: [NgIf, AsyncPipe],
})
export class PlanTipComponent implements OnInit {
  planPhase$: Observable<PlanPhase>;
  planPhaseEnum = PlanPhase;

  constructor(
    private plannerService: PlannerService,
    private mapZoomService: MapZoomService,
    private i18nService: I18nService
  ) {}

  ngOnInit(): void {
    this.planPhase$ = combineLatest([
      this.mapZoomService.zoomLevel$,
      this.plannerService.context.plan$,
    ]).pipe(
      map(([zoomLevel, plan]) => this.determinePlanPhase(zoomLevel, plan)),
      delay(0)
    );
  }

  more(): string {
    const languageSpecificSubject =
      this.i18nService.translation(`@@wiki.planner.edit`);
    return `https://wiki.openstreetmap.org/wiki/${languageSpecificSubject}`;
  }

  private determinePlanPhase(zoomLevel: number, plan: Plan): PlanPhase {
    let planPhase: PlanPhase;
    if (plan.sourceNode === null) {
      if (zoomLevel < ZoomLevel.vectorTileMinZoom) {
        planPhase = PlanPhase.zoomInClickStartNode;
      } else {
        planPhase = PlanPhase.clickStartNode;
      }
    } else if (plan.legs.isEmpty()) {
      if (zoomLevel < ZoomLevel.vectorTileMinZoom) {
        planPhase = PlanPhase.zoomInClickEndNode;
      } else {
        planPhase = PlanPhase.clickEndNode;
      }
    } else {
      planPhase = PlanPhase.extendRoute;
    }
    return planPhase;
  }
}
