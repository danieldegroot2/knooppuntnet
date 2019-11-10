// this class is generated, please do not modify

import {CountryStatistic} from "./country-statistic";

export class Statistic {

  constructor(readonly total: string,
              readonly nl: CountryStatistic,
              readonly be: CountryStatistic,
              readonly de: CountryStatistic,
              readonly fr: CountryStatistic) {
  }

  public static fromJSON(jsonObject): Statistic {
    if (!jsonObject) {
      return undefined;
    }
    return new Statistic(
      jsonObject.total,
      CountryStatistic.fromJSON(jsonObject.nl),
      CountryStatistic.fromJSON(jsonObject.be),
      CountryStatistic.fromJSON(jsonObject.de),
      CountryStatistic.fromJSON(jsonObject.fr)
    );
  }
}
