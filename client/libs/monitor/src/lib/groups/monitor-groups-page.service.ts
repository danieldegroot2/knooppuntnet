import { signal } from '@angular/core';
import { Injectable } from '@angular/core';
import { MonitorService } from '../monitor.service';
import { initialState } from './monitor-groups-page.state';
import { MonitorGroupsPageState } from './monitor-groups-page.state';

@Injectable()
export class MonitorGroupsPageService {
  private readonly _state = signal<MonitorGroupsPageState>(initialState);
  readonly state = this._state.asReadonly();
  readonly admin = this.monitorService.admin;

  constructor(private monitorService: MonitorService) {
    this.monitorService
      .groups()
      .subscribe((response) =>
        this._state.update((state) => ({ ...state, response }))
      );
  }
}
