import { computed } from '@angular/core';
import { signal } from '@angular/core';
import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { Params } from '@angular/router';
import { PeriodParameters } from '@api/common/status';
import { LogPage } from '@api/common/status';
import { ApiResponse } from '@api/custom';
import { ApiService } from '@app/services';
import { RouterService } from '../../shared/services/router.service';
import { StatusLinks } from './status-links';

@Injectable()
export class LogPageService {
  private readonly apiService = inject(ApiService);
  private readonly routerService = inject(RouterService);

  private readonly _response = signal<ApiResponse<LogPage>>(null);
  readonly response = this._response.asReadonly();
  readonly page = computed(() => this.response()?.result);
  readonly statusLinks = computed(() => new StatusLinks(this.page().timestamp, '/status/log'));

  xAxisLabel: string;

  onInit(): void {
    const parameters = this.toPeriodParameters(this.routerService.params());
    if (parameters.period === 'year') {
      this.xAxisLabel = 'weeks';
    } else if (parameters.period === 'month') {
      this.xAxisLabel = 'days';
    } else if (parameters.period === 'week') {
      this.xAxisLabel = 'days';
    } else if (parameters.period === 'day') {
      this.xAxisLabel = 'hours';
    } else if (parameters.period === 'hour') {
      this.xAxisLabel = 'minutes';
    }

    this.apiService.logStatus(parameters).subscribe((response) => this._response.set(response));
  }

  private toPeriodParameters(params: Params): PeriodParameters {
    const period = params['period'];
    if ('year' === period) {
      return {
        period: 'year',
        year: +params['year'],
        month: null,
        week: null,
        day: null,
        hour: null,
      };
    }
    if ('month' === period) {
      return {
        period: 'month',
        year: +params['year'],
        month: +params['monthOrWeek'],
        week: null,
        day: null,
        hour: null,
      };
    }
    if ('week' === period) {
      return {
        period: 'week',
        year: +params['year'],
        month: null,
        week: +params['monthOrWeek'],
        day: null,
        hour: null,
      };
    }
    if ('day' === period) {
      return {
        period: 'day',
        year: +params['year'],
        month: +params['month'],
        week: null,
        day: +params['day'],
        hour: null,
      };
    }
    if ('hour' === period) {
      return {
        period: 'hour',
        year: +params['year'],
        month: +params['month'],
        week: null,
        day: +params['day'],
        hour: +params['hour'],
      };
    }

    const now = new Date();
    return {
      period: 'hour',
      year: now.getFullYear(),
      month: now.getMonth(),
      week: null,
      day: now.getDate(),
      hour: now.getHours(),
    };
  }
}
