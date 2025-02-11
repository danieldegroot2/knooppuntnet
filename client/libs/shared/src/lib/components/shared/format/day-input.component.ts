import { ChangeDetectionStrategy } from '@angular/core';
import { Inject } from '@angular/core';
import { Input } from '@angular/core';
import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { DateAdapter } from '@angular/material/core';
import { MatNativeDateModule } from '@angular/material/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { KpnDateAdapter } from '@app/components/shared/day';
import { DayUtil } from '..';

@Component({
  selector: 'kpn-day-input',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <mat-form-field>
      <mat-label>{{ label }}</mat-label>
      <input matInput [matDatepicker]="picker" [formControl]="date" />
      <mat-hint>{{ dateFormatString() }}</mat-hint>
      <mat-datepicker-toggle matSuffix [for]="picker" />
      <mat-datepicker #picker />
    </mat-form-field>
  `,
  providers: [
    MatNativeDateModule,
    {
      provide: DateAdapter,
      useClass: KpnDateAdapter,
      deps: [MAT_DATE_LOCALE],
    },
  ],
  standalone: true,
  imports: [
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    ReactiveFormsModule,
  ],
})
export class DayInputComponent {
  @Input({ required: true }) date: FormControl<Date | null>;
  @Input({ required: true }) label: string;

  constructor(@Inject(MAT_DATE_LOCALE) private matDateLocale: string) {}

  dateFormatString(): string {
    return DayUtil.formatString(this.matDateLocale);
  }
}
