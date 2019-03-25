import Coordinate from 'ol/View';
import {fromLonLat} from 'ol/proj';
import {createXYZ} from 'ol/tilegrid';
import {click, pointerMove} from 'ol/events/condition';
import {Fill, Icon, Stroke, Style} from 'ol/style';
import Feature from 'ol/Feature';
import {Circle, LineString, Geometry} from 'ol/geom';
import {GeometryCollection} from 'ol/geom/GeometryCollection.js';
import {OSM} from 'ol/source';

export class Crosshair {

  private circleRadius = 200;

  private crossStyle = new Style({
    stroke: new Stroke({
      color: "rgba(255, 0, 0, 0.7)",
      width: 3
    })
  });

  private cross1a = new LineString([[0, 0], [0, 0]]);
  private cross1b = new LineString([[0, 0], [0, 0]]);
  private cross2a = new LineString([[0, 0], [0, 0]]);
  private cross2b = new LineString([[0, 0], [0, 0]]);
  private circle = new Circle([0, 0], this.circleRadius);

  public getFeatures() {
    return [
      this.toFeature(this.cross1a),
      this.toFeature(this.cross1b),
      this.toFeature(this.cross2a),
      this.toFeature(this.cross2b),
      this.toFeature(this.circle),
    ];
  }

  public updatePosition(coordinate: Coordinate) {

    const x = coordinate[0];
    const y = coordinate[1];
    const r = this.circleRadius;

    this.cross1a.setCoordinates([[x - r - r, y], [x - r, y]]);
    this.cross1b.setCoordinates([[x + r, y], [x + r + r, y]]);
    this.cross2a.setCoordinates([[x, y - r - r], [x, y - r]]);
    this.cross2b.setCoordinates([[x, y + r], [x, y + r + r]]);
    this.circle.setCenter(coordinate);
  }

  private toFeature(geometry: Geometry): Feature {
    const feature = new Feature(geometry);
    feature.setStyle(this.crossStyle);
    return feature;
  }

}
