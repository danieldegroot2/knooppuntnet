import { PlanNode } from '@api/common/planner';
import { List } from 'immutable';
import { Coordinate } from 'ol/coordinate';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { switchMap } from 'rxjs/operators';
import { PlannerCommandMoveViaRoute } from '../../commands/planner-command-move-via-route';
import { PlannerContext } from '../../context/planner-context';
import { RouteFeature } from '../../features/route-feature';
import { PlanFlag } from '../../plan/plan-flag';
import { PlanLeg } from '../../plan/plan-leg';
import { PlanUtil } from '../../plan/plan-util';

export class DropViaRouteOnRoute {
  constructor(private readonly context: PlannerContext) {}

  drop(
    oldLeg: PlanLeg,
    routeFeatures: List<RouteFeature>,
    coordinate: Coordinate
  ): void {
    this.buildViaRouteLeg(oldLeg, routeFeatures, coordinate)
      .pipe(
        switchMap((newLeg1) =>
          this.buildNodeToNodeLeg(
            newLeg1.sinkNode,
            oldLeg.sinkNode,
            oldLeg.sinkFlag
          ).pipe(
            map(
              (newLeg2) =>
                new PlannerCommandMoveViaRoute(oldLeg, newLeg1, newLeg2)
            )
          )
        )
      )
      .subscribe({
        next: (command) => this.context.execute(command),
        error: (error) => this.context.errorDialog(error),
      });
  }

  private buildViaRouteLeg(
    oldLeg: PlanLeg,
    routeFeatures: List<RouteFeature>,
    coordinate: Coordinate
  ): Observable<PlanLeg> {
    const source = PlanUtil.legEndNode(+oldLeg.sourceNode.nodeId);
    const sink = PlanUtil.legEndRoutes(routeFeatures.toArray());
    const viaFlag = PlanUtil.viaFlag(coordinate);
    const sinkFlag = PlanUtil.invisibleFlag(coordinate);

    return this.context
      .fetchLeg(source, sink)
      .pipe(map((data) => PlanUtil.leg(data, sinkFlag, viaFlag)));
  }

  private buildNodeToNodeLeg(
    sourceNode: PlanNode,
    sinkNode: PlanNode,
    sinkFlag: PlanFlag
  ): Observable<PlanLeg> {
    const source = PlanUtil.legEndNode(+sourceNode.nodeId);
    const sink = PlanUtil.legEndNode(+sinkNode.nodeId);

    return this.context
      .fetchLeg(source, sink)
      .pipe(map((data) => PlanUtil.leg(data, sinkFlag, null)));
  }
}
