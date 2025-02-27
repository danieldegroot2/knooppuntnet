import { NgFor } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'kpn-route-structure',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <table class="kpn-table">
      <tbody>
        <tr *ngFor="let structureString of structureStrings">
          <td>
            <span [innerHTML]="formatted(structureString)"></span>
          </td>
        </tr>
      </tbody>
    </table>
  `,
  standalone: true,
  imports: [NgFor],
})
export class RouteStructureComponent {
  @Input() structureStrings: string[];

  constructor(private sanitizer: DomSanitizer) {}

  formatted(structureString: string): SafeHtml {
    let html = structureString;
    html = html.replace(/forward/g, '<b>forward</b>');
    html = html.replace(/backward/g, '<b>backward</b>');
    html = html.replace(/unused/g, '<b>unused</b>');
    html = html.replace(/tentacle/g, '<b>tentacle</b>');
    html = html.replace(/broken/g, "<span style='color:red'>broken</span>");
    html = html.replace(/\\+/g, ' + ');
    html = html.replace(/\\-/g, ' - ');
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }
}
