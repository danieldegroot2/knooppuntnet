// this class is generated, please do not modify

import {List} from "immutable";
import {Fact} from "../../custom/fact";
import {RouteInfoAnalysis} from "./route-info-analysis";
import {RouteSummary} from "../route-summary";
import {Tags} from "../../custom/tags";
import {Timestamp} from "../../custom/timestamp";

export class RouteInfo {

  constructor(readonly summary: RouteSummary,
              readonly active: boolean,
              readonly orphan: boolean,
              readonly version: number,
              readonly changeSetId: number,
              readonly lastUpdated: Timestamp,
              readonly tags: Tags,
              readonly facts: List<Fact>,
              readonly analysis: RouteInfoAnalysis) {
  }

  public static fromJSON(jsonObject): RouteInfo {
    if (!jsonObject) {
      return undefined;
    }
    return new RouteInfo(
      RouteSummary.fromJSON(jsonObject.summary),
      jsonObject.active,
      jsonObject.orphan,
      jsonObject.version,
      jsonObject.changeSetId,
      Timestamp.fromJSON(jsonObject.lastUpdated),
      Tags.fromJSON(jsonObject.tags),
      jsonObject.facts ? List(jsonObject.facts.map(json => Fact.fromJSON(json))) : List(),
      RouteInfoAnalysis.fromJSON(jsonObject.analysis)
    );
  }
}
