import Feature from "ol/Feature";
import Circle from "ol/style/Circle";
import Fill from "ol/style/Fill";
import Style from "ol/style/Style";
import {MapService} from "../map.service";
import {MainStyleColors} from "./main-style-colors";
import {NodeStyle} from "./node-style";

export class MainMapNodeStyle {

  constructor(private mapService: MapService) {
  }

  private readonly largeMinZoomLevel = 13;

  private readonly smallNodeSelectedStyle = this.nodeSelectedStyle(8);
  private readonly largeNodeSelectedStyle = this.nodeSelectedStyle(20);
  private readonly smallNodeStyle = NodeStyle.smallNodeStyle();
  private readonly largeNodeStyle = NodeStyle.largeNodeStyle();

  public nodeStyle(zoom: number, feature: Feature, enabled: boolean): Array<Style> {

    const featureId = feature.get("id");
    const layer = feature.get("layer");
    const large = zoom >= this.largeMinZoomLevel;

    const selectedStyle = this.determineNodeSelectedStyle(featureId, large);
    const style = this.determineNodeMainStyle(feature, layer, enabled, large);

    return selectedStyle ? [selectedStyle, style] : [style];
  }

  private determineNodeSelectedStyle(featureId: string, large: boolean): Style {
    let style = null;
    if (this.mapService.selectedNodeId && featureId && featureId === this.mapService.selectedNodeId) {
      if (large) {
        style = this.largeNodeSelectedStyle;
      } else {
        style = this.smallNodeSelectedStyle;
      }
    }
    return style;
  }

  private determineNodeMainStyle(feature: Feature, layer: string, enabled: boolean, large: boolean): Style {
    let style: Style = null;
    if (large) {
      style = this.determineLargeNodeStyle(feature, layer, enabled);
    } else {
      style = this.determineSmallNodeStyle(layer, enabled);
    }
    return style;
  }

  private determineLargeNodeStyle(feature: Feature, layer: string, enabled: boolean): Style {

    const color = this.nodeColor(layer, enabled);

    this.largeNodeStyle.getText().setText(feature.get("name"));
    this.largeNodeStyle.getImage().getStroke().setColor(color);

    if (this.mapService.highlightedNodeId && feature.get("id") === this.mapService.highlightedNodeId) {
      this.largeNodeStyle.getImage().getStroke().setWidth(5);
      this.largeNodeStyle.getImage().setRadius(16);
    } else {
      this.largeNodeStyle.getImage().getStroke().setWidth(3);
      this.largeNodeStyle.getImage().setRadius(14);
    }
    return this.largeNodeStyle;
  }

  private determineSmallNodeStyle(layer: string, enabled: boolean): Style {
    const color = this.nodeColor(layer, enabled);
    this.smallNodeStyle.getImage().getStroke().setColor(color);
    return this.smallNodeStyle;
  }

  private nodeSelectedStyle(radius: number) {
    return new Style({
      image: new Circle({
        radius: radius,
        fill: new Fill({
          color: MainStyleColors.yellow
        })
      })
    });
  }

  private nodeColor(layer: string, enabled: boolean) {
    let nodeColor = MainStyleColors.gray;
    if (enabled) {
      if ("error-node" === layer) {
        nodeColor = MainStyleColors.blue;
      } else if ("orphan-node" === layer) {
        nodeColor = MainStyleColors.darkGreen;
      } else if ("error-orphan-node" === layer) {
        nodeColor = MainStyleColors.darkBlue;
      } else {
        nodeColor = MainStyleColors.green;
      }
    }
    return nodeColor;
  }

}
