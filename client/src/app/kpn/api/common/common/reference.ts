// this class is generated, please do not modify

import {NetworkType} from "../../custom/network-type";

export class Reference {

  constructor(readonly id: number,
              readonly name: string,
              readonly networkType: NetworkType,
              readonly connection: boolean) {
  }

  public static fromJSON(jsonObject): Reference {
    if (!jsonObject) {
      return undefined;
    }
    return new Reference(
      jsonObject.id,
      jsonObject.name,
      NetworkType.fromJSON(jsonObject.networkType),
      jsonObject.connection
    );
  }
}
