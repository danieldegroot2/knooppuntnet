import { GeometryDiff } from '@api/common/route';
import { PointSegment } from '@api/common/route';
import { OlUtil } from '@app/components/ol';
import { I18nService } from '@app/i18n';
import { List } from 'immutable';
import { Color } from 'ol/color';
import Feature from 'ol/Feature';
import LineString from 'ol/geom/LineString';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import { Stroke } from 'ol/style';
import { Style } from 'ol/style';
import { Layers } from './layers';
import { MapLayer } from './map-layer';

export class RouteChangeLayers {
  constructor(private i18nService: I18nService) {}

  build(geometryDiff: GeometryDiff): List<MapLayer> {
    const unchanged = this.segmentLayer(
      '@@map.layer.unchanged',
      geometryDiff.common,
      5,
      [0, 0, 255]
    );
    const added = this.segmentLayer(
      '@@map.layer.added',
      geometryDiff.after,
      12,
      [0, 255, 0]
    );
    const deleted = this.segmentLayer(
      '@@map.layer.deleted',
      geometryDiff.before,
      3,
      [255, 0, 0]
    );

    return List([unchanged, added, deleted]).filter((layer) => layer !== null);
  }

  private segmentLayer(
    name: string,
    segments: PointSegment[],
    width: number,
    color: Color
  ): MapLayer {
    if (segments.length === 0) {
      return null;
    }

    const style = new Style({
      stroke: new Stroke({
        color,
        width,
      }),
    });

    const source = new VectorSource();
    segments.forEach((segment) => {
      const p1 = OlUtil.latLonToCoordinate(segment.p1);
      const p2 = OlUtil.latLonToCoordinate(segment.p2);
      const feature = new Feature(new LineString([p1, p2]));
      feature.setStyle(style);
      source.addFeature(feature);
    });

    const layer = new VectorLayer({
      zIndex: Layers.zIndexNetworkLayer,
      source,
    });
    return MapLayer.simpleLayer(this.i18nService.translation(name), layer);
  }
}
