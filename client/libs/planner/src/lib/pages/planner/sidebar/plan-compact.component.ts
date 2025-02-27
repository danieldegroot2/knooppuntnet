import { NgIf } from '@angular/common';
import { NgFor } from '@angular/common';
import { Input } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { PlanRoute } from '@api/common/planner';
import { Plan } from '../../../domain/plan/plan';
import { PlannerService } from '../../../planner.service';

@Component({
  selector: 'kpn-plan-compact',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span *ngIf="plan.sourceNode !== null" class="node">
      {{ plan.sourceNode.nodeName }}
    </span>
    <ng-container *ngFor="let leg of plan.legs">
      <ng-container *ngFor="let legRoute of leg.routes; let i = index">
        <span *ngIf="hasColour(legRoute)" class="colour">
          {{ colours(legRoute) }}
        </span>
        <span class="node" [class.visited-node]="i < leg.routes.size - 1">
          {{ legRoute.sinkNode.nodeName }}
        </span>
      </ng-container>
    </ng-container>
  `,
  styles: [
    `
      .node {
        padding-right: 5px;
        font-weight: bold;
      }

      .visited-node {
        font-weight: normal;
      }

      .colour {
        padding-right: 5px;
        color: rgba(0, 0, 0, 0.75);
      }
    `,
  ],
  standalone: true,
  imports: [NgIf, NgFor],
})
export class PlanCompactComponent {
  @Input() plan: Plan;

  constructor(private plannerService: PlannerService) {}

  hasColour(planRoute: PlanRoute): boolean {
    return this.plannerService.hasColour(planRoute);
  }

  colours(planRoute: PlanRoute): string {
    return this.plannerService.colours(planRoute);
  }
}
