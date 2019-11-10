// this class is generated, please do not modify

export class TrackPoint {

  constructor(readonly lat: string,
              readonly lon: string) {
  }

  public static fromJSON(jsonObject): TrackPoint {
    if (!jsonObject) {
      return undefined;
    }
    return new TrackPoint(
      jsonObject.lat,
      jsonObject.lon
    );
  }
}
