import { NgIf } from '@angular/common';
import { AfterViewChecked } from '@angular/core';
import { ElementRef } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Input } from '@angular/core';
import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MonitorRouteSaveStep } from '../monitor-route-save-step';

@Component({
  selector: 'kpn-monitor-route-form-save-step',
  template: `
    <div #stepDiv class="kpn-line kpn-spacer-below">
      <div class="icon">
        <mat-spinner *ngIf="step.status === 'busy'" diameter="20"></mat-spinner>
        <mat-icon *ngIf="step.status === 'todo'" svgIcon="dot" class="todo" />
        <mat-icon *ngIf="step.status === 'done'" svgIcon="tick" class="done" />
      </div>
      <span>{{ step.description }}</span>
    </div>
  `,
  styles: [
    `
      .icon {
        width: 2em;
        height: 1.5em;
      }

      .done {
        color: green;
      }

      .todo {
        color: grey;
        width: 0.3em;
        height: 0.3em;
      }
    `,
  ],
  standalone: true,
  imports: [MatIconModule, MatProgressSpinnerModule, NgIf],
})
export class MonitorRouteFormSaveStepComponent implements AfterViewChecked {
  @Input({ required: true }) step: MonitorRouteSaveStep;
  @ViewChild('stepDiv') stepDiv!: ElementRef<HTMLDivElement>;

  ngAfterViewChecked() {
    if (this.step.status === 'busy') {
      this.stepDiv.nativeElement.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
      });
    }
  }
}
