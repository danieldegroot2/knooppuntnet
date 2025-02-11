import { inject } from '@angular/core';
import { signal } from '@angular/core';
import { Injectable } from '@angular/core';
import { MonitorChangesParameters } from '@api/common/monitor';
import { NavService } from '@app/components/shared';
import { PreferencesService } from '@app/core';
import { MonitorService } from '../../monitor.service';
import { MonitorGroupChangesPageState } from './monitor-group-changes-page.state';
import { initialState } from './monitor-group-changes-page.state';

@Injectable()
export class MonitorGroupChangesPageService {
  private readonly navService = inject(NavService);
  private readonly monitorService = inject(MonitorService);
  private readonly preferencesService = inject(PreferencesService);

  private readonly _state = signal<MonitorGroupChangesPageState>(initialState);
  readonly state = this._state.asReadonly();

  readonly impact = this.preferencesService.impact;

  constructor() {
    const groupName = this.navService.param('groupName');
    const groupDescription = this.navService.state('groupDescription');
    this._state.update((state) => ({
      ...state,
      groupName,
      groupDescription,
    }));
    this.load();
  }

  impactChanged(impact: boolean): void {
    this.preferencesService.setImpact(impact);
    this.load();
  }

  pageChanged(pageIndex: number) {
    this._state.update((state) => ({
      ...state,
      pageIndex,
    }));
    this.load();
  }

  private load(): void {
    const parameters: MonitorChangesParameters = {
      pageSize: this.preferencesService.pageSize(),
      pageIndex: this.state().pageIndex,
      impact: this.preferencesService.impact(),
    };
    this.monitorService
      .groupChanges(this.state().groupName, parameters)
      .subscribe((response) => {
        const groupDescription =
          response?.result?.groupDescription ?? this.state().groupDescription;
        this._state.update((state) => ({
          ...state,
          groupDescription,
          response,
        }));
      });
  }
}
