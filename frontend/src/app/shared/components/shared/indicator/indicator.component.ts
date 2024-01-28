import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { Output } from '@angular/core';
import { input } from '@angular/core';
import { IndicatorIconComponent } from './indicator-icon.component';

@Component({
  selector: 'kpn-indicator',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="indicator" (click)="onOpenDialog()">
      <kpn-indicator-icon [letter]="letter()" [color]="color()" />
    </div>
  `,
  styles: `
    .indicator {
      display: inline-block;
      padding-left: 5px;
      padding-right: 5px;
    }
  `,
  standalone: true,
  imports: [IndicatorIconComponent],
})
export class IndicatorComponent {
  letter = input.required<string>();
  color = input<string | undefined>();

  @Output() openDialog = new EventEmitter<void>();

  onOpenDialog() {
    this.openDialog.emit();
  }
}
