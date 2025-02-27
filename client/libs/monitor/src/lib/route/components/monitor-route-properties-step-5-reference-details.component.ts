import { NgClass } from '@angular/common';
import { NgIf } from '@angular/common';
import { Input } from '@angular/core';
import { Component } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';
import { FormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatStepperModule } from '@angular/material/stepper';
import { Timestamp } from '@api/custom';
import { FormStatusComponent } from '@app/components/shared';
import { TimestampPipe } from '@app/components/shared/format';
import { DayInputComponent } from '@app/components/shared/format';

@Component({
  selector: 'kpn-monitor-route-properties-step-5-reference-details',
  template: `
    <div [ngClass]="{ hidden: referenceType.value !== 'osm-now' }">
      <p i18n="@@monitor.route.properties.reference-details.osm-now">
        The state of the OSM relation at this moment will be used as the
        reference for the monitor analysis.
      </p>
      <p i18n="@@monitor.route.properties.reference-details.osm-now.next-step">
        Continue with next step.
      </p>
    </div>

    <div [ngClass]="{ hidden: referenceType.value !== 'osm-past' }">
      <p *ngIf="oldReferenceTimestamp" class="kpn-spacer-below">
        <span
          class="kpn-label"
          i18n="@@monitor.route.properties.reference-details.osm-now.timestamp"
          >Current reference timestamp</span
        >
        {{ oldReferenceTimestamp | yyyymmddhhmm }}
      </p>

      <p i18n="@@monitor.route.properties.reference-details.day">
        Select the date (midnight) of the route relation state that will serve
        as a reference:
      </p>
      <kpn-day-input
        id="osm-reference-date"
        [date]="osmReferenceDate"
        label="Reference day"
        i18n-label="@@monitor.route.properties.reference-details.day.label"
      />
      <div
        *ngIf="
          osmReferenceDate.invalid &&
          (osmReferenceDate.touched || ngForm.submitted)
        "
        class="kpn-form-error"
      >
        <div
          *ngIf="osmReferenceDate.errors?.required"
          id="osm-reference-date-required-error"
          i18n="@@monitor.route.reference-day.required"
        >
          Please provide a valid reference day
        </div>
      </div>
    </div>

    <div [ngClass]="{ hidden: referenceType.value !== 'gpx' }">
      <p i18n="@@monitor.route.properties.reference-details.file">
        Select the file that contains the GPX trace:
      </p>
      <div class="kpn-small-spacer-above">
        <input
          type="file"
          id="gpx-file-input"
          class="file-input"
          (change)="selectFile($event)"
          #fileInput
        />
        <button
          mat-stroked-button
          (click)="fileInput.click()"
          type="button"
          i18n="@@monitor.route.properties.reference-details.file.select"
        >
          Select file
        </button>
      </div>
      <div class="kpn-small-spacer-above kpn-spacer-below">
        <span
          id="gpx-file-name"
          class="kpn-label"
          i18n="@@monitor.route.properties.reference-details.file.name"
          >File</span
        >
        {{ referenceFilename.value }}
      </div>
      <div
        *ngIf="
          referenceFilename.invalid &&
          (referenceFilename.dirty ||
            referenceFilename.touched ||
            ngForm.submitted)
        "
        class="kpn-form-error"
      >
        <div
          *ngIf="referenceFilename.errors?.required"
          id="reference-filename.required"
          i18n="@@monitor.route.reference-filename.required"
        >
          Reference filename is required
        </div>
      </div>

      <p i18n="@@monitor.route.properties.reference-details.gpx.reference-day">
        Select the date at which the gpx trace was recorded or was known to be
        valid:
      </p>
      <kpn-day-input
        id="gpx-reference-date"
        [date]="gpxReferenceDate"
        label="Reference day"
        i18n-label="@@monitor.route.properties.reference-details.day.label"
      />
      <div
        *ngIf="
          gpxReferenceDate.invalid &&
          (gpxReferenceDate.touched || ngForm.submitted)
        "
        class="kpn-form-error"
      >
        <div
          *ngIf="gpxReferenceDate.errors?.required"
          id="reference-day.required"
          i18n="@@monitor.route.reference-day.required"
        >
          Please provide a valid reference day
        </div>
      </div>
    </div>

    <div
      id="multi-gpx.comment"
      [ngClass]="{ hidden: referenceType.value !== 'multi-gpx' }"
    >
      <p
        i18n="@@monitor.route.properties.reference-details.multi-gpx.comment.1"
      >
        Further reference details can be provided later.
      </p>
      <p
        i18n="@@monitor.route.properties.reference-details.multi-gpx.comment.2"
      >
        The GPX traces per route in the superroute can be uploaded from the
        route details page after saving this route definition.
      </p>
      <p
        i18n="@@monitor.route.properties.reference-details.multi-gpx.next-step"
      >
        Continue with next step.
      </p>
    </div>

    <kpn-form-status
      formName="step5-form"
      [statusChanges]="ngForm.statusChanges"
    ></kpn-form-status>
    <div class="kpn-button-group">
      <button
        id="step5-back"
        mat-stroked-button
        matStepperPrevious
        i18n="@@action.back"
      >
        Back
      </button>
      <button
        id="step5-next"
        mat-stroked-button
        matStepperNext
        i18n="@@action.next"
      >
        Next
      </button>
    </div>
  `,
  styles: [
    `
      .file-input {
        display: none;
      }
    `,
  ],
  standalone: true,
  imports: [
    DayInputComponent,
    FormStatusComponent,
    MatButtonModule,
    MatStepperModule,
    NgClass,
    NgIf,
    TimestampPipe,
  ],
})
export class MonitorRoutePropertiesStep5ReferenceDetailsComponent {
  @Input({ required: true }) ngForm: FormGroupDirective;
  @Input({ required: true }) referenceType: FormControl<string>;
  @Input({ required: true }) osmReferenceDate: FormControl<Date | null>;
  @Input({ required: true }) gpxReferenceDate: FormControl<Date | null>;
  @Input({ required: true }) referenceFilename: FormControl<string>;
  @Input({ required: true }) referenceFile: FormControl<File>;
  @Input({ required: true }) oldReferenceTimestamp: Timestamp | null;

  selectFile(selectEvent: any) {
    this.referenceFile.setValue(selectEvent.target.files[0]);
    this.referenceFilename.setValue(selectEvent.target.files[0].name);
  }
}
