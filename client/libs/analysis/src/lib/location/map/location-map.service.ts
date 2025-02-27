import { Injectable } from '@angular/core';
import { Bounds } from '@api/common';
import { NetworkType } from '@api/custom';
import { ZoomLevel } from '@app/components/ol/domain';
import { BackgroundLayer } from '@app/components/ol/layers';
import { OsmLayer } from '@app/components/ol/layers';
import { NetworkVectorTileLayer } from '@app/components/ol/layers';
import { NetworkBitmapTileLayer } from '@app/components/ol/layers';
import { LocationBoundaryLayer } from '@app/components/ol/layers';
import { MapControls } from '@app/components/ol/layers';
import { MapLayerRegistry } from '@app/components/ol/layers';
import { OpenlayersMapService } from '@app/components/ol/services';
import { MapClickService } from '@app/components/ol/services';
import { MainMapStyleParameters } from '@app/components/ol/style';
import { MainMapStyle } from '@app/components/ol/style';
import { Util } from '@app/components/shared';
import { SurveyDateValues } from '@app/core';
import Map from 'ol/Map';
import View from 'ol/View';
import { Observable } from 'rxjs';
import { of } from 'rxjs';

@Injectable()
export class LocationMapService extends OpenlayersMapService {
  constructor(private mapClickService: MapClickService) {
    super();
  }

  init(
    networkType: NetworkType,
    surveyDateValues: SurveyDateValues,
    geoJson: string,
    bounds: Bounds
  ): void {
    this.registerLayers(networkType, surveyDateValues, geoJson);

    this.initMap(
      new Map({
        target: this.mapId,
        layers: this.layers,
        controls: MapControls.build(),
        view: new View({
          minZoom: ZoomLevel.minZoom,
          maxZoom: ZoomLevel.vectorTileMaxOverZoom,
        }),
      })
    );

    this.map.getView().fit(Util.toExtent(bounds, 0.05));
    this.mapClickService.installOn(this.map);
    this.finalizeSetup();
  }

  private registerLayers(
    networkType: NetworkType,
    surveyDateValues: SurveyDateValues,
    geoJson: string
  ): void {
    const parameters$: Observable<MainMapStyleParameters> = of(
      new MainMapStyleParameters(
        'analysis',
        true,
        surveyDateValues,
        null,
        null,
        null
      )
    );
    const mainMapStyle = new MainMapStyle(parameters$);
    const networkLayers = [
      NetworkVectorTileLayer.build(networkType, mainMapStyle.styleFunction()),
      NetworkBitmapTileLayer.build(networkType, 'analysis'),
    ];

    const registry = new MapLayerRegistry();
    registry.register([], BackgroundLayer.build(), true);
    registry.register([], OsmLayer.build(), false);
    registry.registerAll([], networkLayers, true);
    registry.register([], LocationBoundaryLayer.build(geoJson), true);
    this.register(registry);
  }
}
