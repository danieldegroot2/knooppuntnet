// this class is generated, please do not modify

import {List} from "immutable";
import {Change} from "../../custom/change";
import {Timestamp} from "../../custom/timestamp";

export class ChangeSet {

  constructor(readonly id: number,
              readonly timestamp: Timestamp,
              readonly timestampFrom: Timestamp,
              readonly timestampUntil: Timestamp,
              readonly timestampBefore: Timestamp,
              readonly timestampAfter: Timestamp,
              readonly changes: List<Change>) {
  }

  public static fromJSON(jsonObject): ChangeSet {
    if (!jsonObject) {
      return undefined;
    }
    return new ChangeSet(
      jsonObject.id,
      Timestamp.fromJSON(jsonObject.timestamp),
      Timestamp.fromJSON(jsonObject.timestampFrom),
      Timestamp.fromJSON(jsonObject.timestampUntil),
      Timestamp.fromJSON(jsonObject.timestampBefore),
      Timestamp.fromJSON(jsonObject.timestampAfter),
      jsonObject.changes ? List(jsonObject.changes.map(json => Change.fromJSON(json))) : List()
    );
  }
}
