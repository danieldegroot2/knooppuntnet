// this class is generated, please do not modify

import {List} from "immutable";
import {PointSegment} from "./point-segment";

export class GeometryDiff {

  constructor(readonly common: List<PointSegment>,
              readonly before: List<PointSegment>,
              readonly after: List<PointSegment>) {
  }

  public static fromJSON(jsonObject): GeometryDiff {
    if (!jsonObject) {
      return undefined;
    }
    return new GeometryDiff(
      jsonObject.common ? List(jsonObject.common.map(json => PointSegment.fromJSON(json))) : List(),
      jsonObject.before ? List(jsonObject.before.map(json => PointSegment.fromJSON(json))) : List(),
      jsonObject.after ? List(jsonObject.after.map(json => PointSegment.fromJSON(json))) : List()
    );
  }
}
