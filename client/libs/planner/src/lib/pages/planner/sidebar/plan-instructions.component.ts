import { NgFor } from '@angular/common';
import { OnChanges } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { SimpleChanges } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { List } from 'immutable';
import { DirectionsAnalyzer } from '../../../domain/directions/directions-analyzer';
import { Plan } from '../../../domain/plan/plan';
import { PlanInstruction } from '../../../domain/plan/plan-instruction';
import { PlanInstructionComponent } from './plan-instruction.component';

@Component({
  selector: 'kpn-plan-instructions',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngFor="let instruction of instructions">
      <kpn-plan-instruction [instruction]="instruction" />
      <mat-divider />
    </div>
  `,
  styles: [``],
  standalone: true,
  imports: [NgFor, PlanInstructionComponent, MatDividerModule],
})
export class PlanInstructionsComponent implements OnChanges {
  @Input() plan: Plan;
  instructions: List<PlanInstruction>;

  ngOnChanges(changes: SimpleChanges): void {
    this.instructions = new DirectionsAnalyzer().analyze(this.plan);
  }
}
