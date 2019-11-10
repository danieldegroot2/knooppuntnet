// this class is generated, please do not modify

import {List} from "immutable";
import {RawNode} from "./raw-node";
import {RawRelation} from "./raw-relation";
import {RawWay} from "./raw-way";
import {Timestamp} from "../../../custom/timestamp";

export class RawData {

  constructor(readonly timestamp: Timestamp,
              readonly nodes: List<RawNode>,
              readonly ways: List<RawWay>,
              readonly relations: List<RawRelation>) {
  }

  public static fromJSON(jsonObject): RawData {
    if (!jsonObject) {
      return undefined;
    }
    return new RawData(
      Timestamp.fromJSON(jsonObject.timestamp),
      jsonObject.nodes ? List(jsonObject.nodes.map(json => RawNode.fromJSON(json))) : List(),
      jsonObject.ways ? List(jsonObject.ways.map(json => RawWay.fromJSON(json))) : List(),
      jsonObject.relations ? List(jsonObject.relations.map(json => RawRelation.fromJSON(json))) : List()
    );
  }
}
