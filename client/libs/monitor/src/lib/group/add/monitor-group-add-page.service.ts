import { inject } from '@angular/core';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Validators } from '@angular/forms';
import { FormControl } from '@angular/forms';
import { NavService } from '@app/components/shared';
import { MonitorService } from '../../monitor.service';

@Injectable()
export class MonitorGroupAddPageService {
  private navService = inject(NavService);
  private monitorService = inject(MonitorService);

  readonly name = new FormControl<string>('', {
    validators: [Validators.required, Validators.maxLength(15)],
    asyncValidators: this.monitorService.asyncGroupNameUniqueValidator(
      () => ''
    ),
  });

  readonly description = new FormControl<string>('', [
    Validators.required,
    Validators.maxLength(100),
  ]);

  readonly form = new FormGroup({
    name: this.name,
    description: this.description,
  });

  add(): void {
    if (this.form.valid) {
      this.monitorService
        .groupAdd(this.form.value)
        .subscribe(() => this.navService.go('/monitor'));
    }
  }
}
