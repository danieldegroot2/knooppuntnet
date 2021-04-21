import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';

@Component({
  selector: 'kpn-icon-buttons',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="icon-buttons">
      <ng-content></ng-content>
    </div>
  `,
  styles: [
    `
      .icon-buttons {
        display: flex;
        flex-wrap: wrap;
      }
    `,
  ],
})
export class IconButtonsComponent {}
