import Geocoder from 'ol-geocoder';
import Map from 'ol/Map';

export class MapGeocoder {
  static install(map: Map): void {
    const geocoder = new Geocoder('nominatim', {
      provider: 'osm',
      lang: 'en',
      placeholder: 'Search for...',
      limit: 5,
      keepOpen: false,
      debug: false,
    });

    map.addControl(geocoder);
    geocoder.on('addresschosen', (evt) =>
      map.getView().animate({ center: evt.coordinate })
    );
  }
}
