import { ChangeDetectionStrategy } from '@angular/core';
import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NetworkNodeRow } from '@api/common/network';
import { IndicatorComponent } from '@app/components/shared/indicator';
import { ProposedIndicatorDialogComponent } from './proposed-indicator-dialog.component';

@Component({
  selector: 'kpn-proposed-indicator',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-indicator
      letter="P"
      i18n-letter="@@proposed-indicator.letter"
      [color]="color"
      (openDialog)="onOpenDialog()"
    />
  `,
  standalone: true,
  imports: [IndicatorComponent],
})
export class ProposedIndicatorComponent implements OnInit {
  @Input() node: NetworkNodeRow;
  color: string;

  constructor(private dialog: MatDialog) {}

  ngOnInit(): void {
    this.color = this.node.detail.proposed ? 'blue' : 'gray';
  }

  onOpenDialog() {
    this.dialog.open(ProposedIndicatorDialogComponent, {
      data: this.color,
      autoFocus: false,
      maxWidth: 600,
    });
  }
}
