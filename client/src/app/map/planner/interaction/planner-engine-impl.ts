import {List} from "immutable";
import {Coordinate} from "ol/coordinate";
import {PlannerCommandAddLeg} from "../commands/planner-command-add-leg";
import {PlannerCommandAddStartPoint} from "../commands/planner-command-add-start-point";
import {PlannerCommandMoveEndPoint} from "../commands/planner-command-move-end-point";
import {PlannerCommandMoveFirstLegSource} from "../commands/planner-command-move-first-leg-source";
import {PlannerCommandMoveStartPoint} from "../commands/planner-command-move-start-point";
import {PlannerCommandMoveViaPoint} from "../commands/planner-command-move-via-point";
import {PlannerCommandSplitLeg} from "../commands/planner-command-split-leg";
import {PlannerContext} from "../context/planner-context";
import {FeatureId} from "../features/feature-id";
import {MapFeature} from "../features/map-feature";
import {PlanFlagType} from "../plan/plan-flag-type";
import {PlanLeg} from "../plan/plan-leg";
import {PlanNode} from "../plan/plan-node";
import {PlannerDragFlag} from "./planner-drag-flag";
import {PlannerDragFlagAnalyzer} from "./planner-drag-flag-analyzer";
import {PlannerDragLeg} from "./planner-drag-leg";
import {PlannerEngine} from "./planner-engine";
import {LegFeature} from "../features/leg-feature";
import {PoiFeature} from "../features/poi-feature";
import {NetworkNodeFeature} from "../features/network-node-feature";
import {FlagFeature} from "../features/flag-feature";

export class PlannerEngineImpl implements PlannerEngine {

  private legDrag: PlannerDragLeg = null;
  private nodeDrag: PlannerDragFlag = null;

  constructor(private context: PlannerContext) {
  }

  handleDownEvent(features: List<MapFeature>, coordinate: Coordinate): boolean {

    if (features.isEmpty()) {
      return false;
    }

    const flag = this.findFlag(features);
    if (flag != null) {
      if (this.flagDragStarted(flag, coordinate)) {
        return true;
      }
    }

    const networkNode = this.findNetworkNode(features);
    if (networkNode != null) {
      this.nodeSelected(networkNode);
      return true;
    }

    const leg = this.findLeg(features);
    if (leg != null) {
      if (this.legDragStarted(leg.id, coordinate)) {
        return true;
      }
    }

    const poiFeature = this.findPoi(features);
    if (poiFeature != null) {
      this.context.overlay.setPosition(poiFeature.coordinate);
      return true;
    }

    return false;
  }

  handleMoveEvent(features: List<MapFeature>, coordinate: Coordinate): boolean {

    if (features.isEmpty()) {
      this.context.cursor.setStyleDefault();
      return false;
    }

    const flagFeature = this.findFlag(features);
    if (!!flagFeature) {
      this.context.cursor.setStyleMove();
      return true;
    }

    const networkNodeFeature = this.findNetworkNode(features);
    if (networkNodeFeature != null) {
      this.context.cursor.setStylePointer();
      return true;
    }

    const leg = this.findLeg(features);
    if (leg != null) {
      this.context.cursor.setStyleMove();
      return true;
    }

    const poiFeature = this.findPoi(features);
    if (poiFeature != null) {
      this.context.cursor.setStylePointer();
      return true;
    }

    this.context.cursor.setStyleDefault();

    return false;
  }

  handleDragEvent(features: List<MapFeature>, coordinate: Coordinate): boolean {

    if (this.isDraggingNode()) {
      const networkNodeFeature = this.findNetworkNode(features);
      if (networkNodeFeature != null) { // snap to node position
        this.context.routeLayer.updateFlagCoordinate(this.nodeDrag.oldNode.featureId, networkNodeFeature.node.coordinate);
        this.context.elasticBand.updatePosition(networkNodeFeature.node.coordinate);
        return true;
      }
      this.context.routeLayer.updateFlagCoordinate(this.nodeDrag.oldNode.featureId, coordinate);
      this.context.elasticBand.updatePosition(coordinate);
      return true;
    }

    if (this.isDraggingLeg()) {
      const networkNodeFeature = this.findNetworkNode(features);
      if (networkNodeFeature != null) { // snap to node position
        this.context.elasticBand.updatePosition(networkNodeFeature.node.coordinate);
        return true;
      }
      this.context.elasticBand.updatePosition(coordinate);
      return true;
    }

    return false;
  }

  handleUpEvent(features: List<MapFeature>, coordinate: Coordinate): boolean {

    if (this.isDraggingLeg() || this.isDraggingNode()) {
      this.context.cursor.setStyleDefault();
      this.context.elasticBand.setInvisible();

      const networkNode = this.findNetworkNode(features);
      if (networkNode != null) {
        if (this.isDraggingLeg()) {
          this.endDragLeg(networkNode.node);
        } else if (this.isDraggingNode()) {
          this.endDragNode(networkNode.node);
        }
        return true;
      }

      if (this.isDraggingNode()) {
        // cancel drag - put flag at its original coordinate again
        this.context.routeLayer.updateFlagCoordinate(this.nodeDrag.oldNode.featureId, this.nodeDrag.oldNode.coordinate);
      }

      this.dragCancel();
    }

    return false;
  }

  handleMouseOut() {
  }

  handleMouseEnter() {
  }

  private nodeSelected(networkNode: NetworkNodeFeature): void {
    if (this.context.plan.source === null) {
      const command = new PlannerCommandAddStartPoint(networkNode.node);
      this.context.execute(command);
    } else {
      const source: PlanNode = this.context.plan.sink;
      const leg = this.buildLeg(FeatureId.next(), source, networkNode.node);
      const command = new PlannerCommandAddLeg(leg.featureId);
      this.context.execute(command);
    }
  }

  private legDragStarted(legId: string, coordinate: Coordinate): boolean {
    const leg = this.context.legs.getById(legId);
    if (leg) {
      const anchor1 = leg.source.coordinate;
      const anchor2 = leg.sink.coordinate;
      this.legDrag = new PlannerDragLeg(legId, anchor1, anchor2);
      this.context.elasticBand.set(anchor1, anchor2, coordinate);
      return true;
    }
    return false;
  }

  private flagDragStarted(flag: FlagFeature, coordinate: Coordinate): boolean {

    this.nodeDrag = new PlannerDragFlagAnalyzer(this.context.plan).dragStarted(flag);
    if (this.nodeDrag !== null) {
      this.context.routeLayer.updateFlagCoordinate(this.nodeDrag.oldNode.featureId, coordinate);
      this.context.elasticBand.set(this.nodeDrag.anchor1, this.nodeDrag.anchor2, coordinate);
      return true;
    }
    return false;
  }

  private isDraggingLeg(): boolean {
    return this.legDrag !== null;
  }

  private isDraggingNode(): boolean {
    return this.nodeDrag !== null;
  }

  private endDragLeg(connection: PlanNode): void {
    if (this.legDrag !== null) {
      const oldLeg = this.context.legs.getById(this.legDrag.oldLegId);
      if (oldLeg) {
        const newLeg1 = this.buildLeg(FeatureId.next(), oldLeg.source, connection);
        const newLeg2 = this.buildLeg(FeatureId.next(), connection, oldLeg.sink);
        const command = new PlannerCommandSplitLeg(oldLeg.featureId, newLeg1.featureId, newLeg2.featureId);
        this.context.execute(command);
      }
      this.legDrag = null;
    }
  }

  private endDragNode(newNode: PlanNode): void {

    if (this.nodeDrag.flagType == PlanFlagType.Start) {
      if (this.context.plan.legs.isEmpty()) {
        const command = new PlannerCommandMoveStartPoint(this.nodeDrag.oldNode, newNode);
        this.context.execute(command);
      } else {
        const oldFirstLeg: PlanLeg = this.context.plan.legs.first();
        const newFirstLeg: PlanLeg = this.buildLeg(FeatureId.next(), newNode, oldFirstLeg.sink);
        const command = new PlannerCommandMoveFirstLegSource(oldFirstLeg.featureId, newFirstLeg.featureId);
        this.context.execute(command);


      }
    } else { // end node
      const oldLastLeg: PlanLeg = this.context.plan.legs.last();
      if (this.nodeDrag.oldNode.featureId == oldLastLeg.sink.featureId) {
        const newLastLeg: PlanLeg = this.buildLeg(FeatureId.next(), oldLastLeg.source, newNode);
        const command = new PlannerCommandMoveEndPoint(oldLastLeg.featureId, newLastLeg.featureId);
        this.context.execute(command);
      } else {
        const legs = this.context.plan.legs;
        const nextLegIndex = legs.findIndex(leg => leg.featureId == this.nodeDrag.legFeatureId);

        const oldLeg1 = legs.get(nextLegIndex - 1);
        const oldLeg2 = legs.get(nextLegIndex);

        const newLeg1: PlanLeg = this.buildLeg(FeatureId.next(), oldLeg1.source, newNode);
        const newLeg2: PlanLeg = this.buildLeg(FeatureId.next(), newNode, oldLeg2.sink);

        const command = new PlannerCommandMoveViaPoint(nextLegIndex, oldLeg1.featureId, oldLeg2.featureId, newLeg1.featureId, newLeg2.featureId);
        this.context.execute(command);
      }
    }

    this.nodeDrag = null;
  }

  private dragCancel(): void {
    if (this.legDrag !== null) {
      this.context.elasticBand.setInvisible();
      this.legDrag = null;
    }

    if (this.nodeDrag !== null) {
      this.context.elasticBand.setInvisible();
      this.nodeDrag = null;
    }
  }

  private buildLeg(legId: string, source: PlanNode, sink: PlanNode): PlanLeg {

    const cachedLeg = this.context.legs.get(source.nodeId, sink.nodeId);
    if (cachedLeg) {
      const plan = new PlanLeg(legId, source, sink, cachedLeg.meters, cachedLeg.routes);
      this.context.legs.add(plan);
    }

    this.context.legRepository.planLeg(this.context.networkType.name, legId, source, sink).subscribe(planLeg => {
      if (planLeg) {
        this.context.legs.add(planLeg);
        this.context.updatePlanLeg(planLeg);
      }
    });

    const cachedLeg2 = this.context.legs.get(source.nodeId, sink.nodeId);
    if (cachedLeg2) {
      return new PlanLeg(legId, source, sink, cachedLeg2.meters, cachedLeg2.routes);
    }

    const leg = new PlanLeg(legId, source, sink, 0, List());
    this.context.legs.add(leg);
    return leg;
  }

  private findFlag(features: List<MapFeature>): FlagFeature {
    const flagFeatures = features.filter(f => f instanceof FlagFeature);
    if (flagFeatures.isEmpty()) {
      return null;
    }
    return flagFeatures.get(0) as FlagFeature; // TODO find the closest
  }

  private findNetworkNode(features: List<MapFeature>): NetworkNodeFeature {
    const nodes = features.filter(f => f instanceof NetworkNodeFeature);
    if (nodes.isEmpty()) {
      return null;
    }
    return nodes.get(0) as NetworkNodeFeature; // TODO find the closest
  }

  private findLeg(features: List<MapFeature>): LegFeature {
    const legs = features.filter(f => f instanceof LegFeature);
    if (legs.isEmpty()) {
      return null;
    }
    return legs.get(0) as LegFeature; // TODO find the closest
  }

  private findPoi(features: List<MapFeature>): PoiFeature {
    const pois = features.filter(f => f instanceof PoiFeature);
    if (pois.isEmpty()) {
      return null;
    }
    return pois.get(0) as PoiFeature; // TODO find the closest
  }

}
