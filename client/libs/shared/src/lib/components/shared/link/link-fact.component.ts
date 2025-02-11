import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'kpn-link-fact',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <a [routerLink]="'/analysis/' + fact + '/' + country + '/' + networkType">{{
      fact
    }}</a>
  `,
  standalone: true,
  imports: [RouterLink],
})
export class LinkFactComponent {
  @Input({ required: true }) fact: string;
  @Input({ required: true }) country: string;
  @Input({ required: true }) networkType: string;
}
