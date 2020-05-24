import {Component} from "@angular/core";

@Component({
  selector: "kpn-map-sidebar-planner",
  // TODO changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <mat-expansion-panel [expanded]="true">
      <mat-expansion-panel-header>
        <mat-panel-title>
          <div class="header">
            <h1 i18n="@@planner.title">Route planner</h1>
            <kpn-doc-link [subject]="'planner'"></kpn-doc-link>
          </div>
        </mat-panel-title>
      </mat-expansion-panel-header>
      <ng-template matExpansionPanelContent>
        <kpn-plan></kpn-plan>
      </ng-template>
    </mat-expansion-panel>
  `,
  styles: [`
    .header {
      display: flex;
      align-items: center;
      width: 100%;
    }

    .header h1 {
      flex: 1;
      display: inline-block;
    }
  `]
})
export class MapSidebarPlannerComponent {
}
