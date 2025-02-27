import { ChangesFilter } from '@api/common/changes/filter';
import { ChangesFilterPeriod } from '@api/common/changes/filter';
import { ChangesParameters } from '@api/common/changes/filter';
import { Util } from '@app/components/shared';
import { List } from 'immutable';
import { ChangeFilterOption } from './change-filter-option';

export class ChangeFilterOptions {
  constructor(readonly options: List<ChangeFilterOption>) {}

  public static empty(): ChangeFilterOptions {
    return new ChangeFilterOptions(List());
  }

  public static from(
    parameters: ChangesParameters,
    filter: ChangesFilter,
    update: (changesParameters: ChangesParameters) => void
  ): ChangeFilterOptions {
    const all = this.buildAll(parameters, filter, update);

    const options = filter.periods.map((year) => {
      const months = year.periods.map((month) => {
        const days = month.periods.map(
          (day) =>
            new ChangeFilterOption(
              'day',
              day,
              List(),
              () =>
                update(
                  this.updatedParameters(
                    parameters,
                    true,
                    year.name,
                    month.name,
                    day.name
                  )
                ),
              () =>
                update(
                  this.updatedParameters(
                    parameters,
                    false,
                    year.name,
                    month.name,
                    day.name
                  )
                )
            )
        );
        return new ChangeFilterOption(
          'month',
          month,
          List(days),
          () =>
            update(
              this.updatedParameters(parameters, true, year.name, month.name)
            ),
          () =>
            update(
              this.updatedParameters(parameters, false, year.name, month.name)
            )
        );
      });

      return new ChangeFilterOption(
        'year',
        year,
        List(months),
        () => update(this.updatedParameters(parameters, true, year.name)),
        () => update(this.updatedParameters(parameters, false, year.name))
      );
    });

    const flatOptions: ChangeFilterOption[] = [];
    if (options.length > 0) {
      flatOptions.push(all);
      options.forEach((year) => {
        flatOptions.push(year);
        year.options.forEach((month) => {
          flatOptions.push(month);
          month.options.forEach((day) => {
            flatOptions.push(day);
          });
        });
      });
    }
    return new ChangeFilterOptions(List(flatOptions));
  }

  private static updatedParameters(
    parameters: ChangesParameters,
    impact: boolean,
    year: number = null,
    month: number = null,
    day: number = null
  ): ChangesParameters {
    return {
      year,
      month,
      day,
      pageSize: parameters.pageSize,
      pageIndex: parameters.pageIndex,
      impact,
    };
  }

  private static buildAll(
    parameters: ChangesParameters,
    filter: ChangesFilter,
    update: (changesParameters: ChangesParameters) => void
  ): ChangeFilterOption {
    const totalCount = Util.sum(
      List(filter.periods.map((period) => period.totalCount))
    );
    const impactedCount = Util.sum(
      List(filter.periods.map((period) => period.impactedCount))
    );

    const all: ChangesFilterPeriod = {
      name: 0,
      totalCount,
      impactedCount,
      current: false,
      selected: false,
      periods: [],
    };

    return new ChangeFilterOption(
      'year',
      all,
      List(),
      () => update(this.updatedParameters(parameters, true)),
      () => update(this.updatedParameters(parameters, false))
    );
  }
}
