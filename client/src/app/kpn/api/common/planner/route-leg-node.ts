// this class is generated, please do not modify

export class RouteLegNode {

  constructor(readonly nodeId: string,
              readonly nodeName: string,
              readonly lat: string,
              readonly lon: string) {
  }

  public static fromJSON(jsonObject): RouteLegNode {
    if (!jsonObject) {
      return undefined;
    }
    return new RouteLegNode(
      jsonObject.nodeId,
      jsonObject.nodeName,
      jsonObject.lat,
      jsonObject.lon
    );
  }
}
