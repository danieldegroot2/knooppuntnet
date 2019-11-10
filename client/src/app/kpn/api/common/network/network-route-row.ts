// this class is generated, please do not modify

import {Timestamp} from "../../custom/timestamp";

export class NetworkRouteRow {

  constructor(readonly id: number,
              readonly name: string,
              readonly length: number,
              readonly role: string,
              readonly investigate: boolean,
              readonly accessible: boolean,
              readonly roleConnection: boolean,
              readonly relationLastUpdated: Timestamp) {
  }

  public static fromJSON(jsonObject): NetworkRouteRow {
    if (!jsonObject) {
      return undefined;
    }
    return new NetworkRouteRow(
      jsonObject.id,
      jsonObject.name,
      jsonObject.length,
      jsonObject.role,
      jsonObject.investigate,
      jsonObject.accessible,
      jsonObject.roleConnection,
      Timestamp.fromJSON(jsonObject.relationLastUpdated)
    );
  }
}
