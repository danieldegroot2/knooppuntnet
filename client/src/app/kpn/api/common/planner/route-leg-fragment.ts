// this class is generated, please do not modify

export class RouteLegFragment {

  constructor(readonly lat: string,
              readonly lon: string,
              readonly meters: number,
              readonly orientation: number,
              readonly streetIndex: number) {
  }

  public static fromJSON(jsonObject): RouteLegFragment {
    if (!jsonObject) {
      return undefined;
    }
    return new RouteLegFragment(
      jsonObject.lat,
      jsonObject.lon,
      jsonObject.meters,
      jsonObject.orientation,
      jsonObject.streetIndex
    );
  }
}
