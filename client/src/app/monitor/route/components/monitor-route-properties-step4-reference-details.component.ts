import { Input } from '@angular/core';
import { Component } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'kpn-monitor-route-properties-step-4-reference-details',
  template: `
    <div [ngClass]="{ hidden: referenceType.value !== 'osm' }">
      <p>
        Select the date of the route relation state that will serve as a
        reference (default today):
      </p>
      <kpn-day-input
        [ngForm]="ngForm"
        [date]="osmReferenceDate"
        label="Reference date"
      >
      </kpn-day-input>
    </div>

    <div [ngClass]="{ hidden: referenceType.value !== 'gpx' }">
      <p>Select the file that contains the GPX trace:</p>
      <input
        type="file"
        class="file-input"
        (change)="selectFile($event)"
        #fileInput
      />
      <button mat-raised-button (click)="fileInput.click()" type="button">
        Select
      </button>
      <p><span class="kpn-label">File</span> {{ gpxFile?.value?.name }}</p>
    </div>

    <div class="kpn-button-group">
      <button mat-stroked-button matStepperPrevious>Back</button>
      <button mat-stroked-button matStepperNext>Next</button>
    </div>
  `,
  styles: [
    `
      .file-input {
        display: none;
      }
    `,
  ],
})
export class MonitorRoutePropertiesStep4ReferenceDetailsComponent {
  @Input() ngForm: FormGroupDirective;
  @Input() referenceType: FormControl<string>;
  @Input() osmReferenceDate: FormControl<Date | null>;
  @Input() gpxFilename: FormControl<string>;
  @Input() gpxFile: FormControl<File>;

  selectFile(selectEvent: any) {
    this.gpxFile.setValue(selectEvent.target.files[0]);
    this.gpxFilename.setValue(selectEvent.target.files[0].name);
  }
}
