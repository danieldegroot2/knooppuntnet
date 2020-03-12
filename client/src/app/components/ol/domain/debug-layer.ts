import TileLayer from "ol/layer/Tile";
import TileDebug from "ol/source/TileDebug";
import {createXYZ} from "ol/tilegrid";

export class DebugLayer {

  public static build(): TileLayer {

    const tileGrid = createXYZ({
      tileSize: 256,
      maxZoom: 20
    });

    return new TileLayer({
      source: new TileDebug({
        zDirection: 1,
        tileGrid: tileGrid
      })
    });
  }

}
