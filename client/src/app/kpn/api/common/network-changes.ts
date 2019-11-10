// this class is generated, please do not modify

import {List} from "immutable";
import {ChangeSetNetwork} from "./change-set-network";

export class NetworkChanges {

  constructor(readonly creates: List<ChangeSetNetwork>,
              readonly updates: List<ChangeSetNetwork>,
              readonly deletes: List<ChangeSetNetwork>) {
  }

  public static fromJSON(jsonObject): NetworkChanges {
    if (!jsonObject) {
      return undefined;
    }
    return new NetworkChanges(
      jsonObject.creates ? List(jsonObject.creates.map(json => ChangeSetNetwork.fromJSON(json))) : List(),
      jsonObject.updates ? List(jsonObject.updates.map(json => ChangeSetNetwork.fromJSON(json))) : List(),
      jsonObject.deletes ? List(jsonObject.deletes.map(json => ChangeSetNetwork.fromJSON(json))) : List()
    );
  }
}
