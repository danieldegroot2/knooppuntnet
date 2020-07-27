import {PlannerContext} from "../../context/planner-context";
import {PlanNode} from "../../../../kpn/api/common/planner/plan-node";
import {PlanUtil} from "../../plan/plan-util";
import {PlannerCommandMoveEndPoint} from "../../commands/planner-command-move-end-point";
import {LegEnd} from "../../../../kpn/api/common/planner/leg-end";
import {Observable} from "rxjs";
import {PlanLeg} from "../../plan/plan-leg";
import {map} from "rxjs/operators";
import {FeatureId} from "../../features/feature-id";

export class MoveEndPoint {

  constructor(private readonly context: PlannerContext) {
  }

  move(sinkNode: PlanNode): void {

    const oldLeg = this.context.plan.legs.get(-1, null);

    if (oldLeg.viaFlag) {
      const sourceNode = oldLeg.sinkNode;
      const source = PlanUtil.legEndNode(+sourceNode.nodeId);
      const sink = PlanUtil.legEndNode(+sinkNode.nodeId);
      this.buildLeg(source, sink).subscribe(newLeg => {
        const command = new PlannerCommandMoveEndPoint(oldLeg.featureId, newLeg.featureId);
        this.context.execute(command);
      });
    } else {
      const sourceNode = oldLeg.sourceNode;
      const source = PlanUtil.legEndNode(+sourceNode.nodeId);
      const sink = PlanUtil.legEndNode(+sinkNode.nodeId);
      this.buildLeg(source, sink).subscribe(newLeg => {
        const command = new PlannerCommandMoveEndPoint(oldLeg.featureId, newLeg.featureId);
        this.context.execute(command);
      });
    }
  }

  private buildLeg(source: LegEnd, sink: LegEnd): Observable<PlanLeg> {
    return this.context.legRepository.planLeg(this.context.networkType, source, sink).pipe(
      map(data => {
        const legKey = PlanUtil.key(source, sink);
        const sinkFlag = PlanUtil.endFlag(data.sinkNode.coordinate);
        const newLeg = new PlanLeg(FeatureId.next(), legKey, source, sink, sinkFlag, null, data.routes);
        this.context.legs.add(newLeg);
        return newLeg;
      })
    );
  }

}
