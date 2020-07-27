import {PlannerContext} from "../../context/planner-context";
import {PlanNode} from "../../../../kpn/api/common/planner/plan-node";
import {PlanUtil} from "../../plan/plan-util";
import {PlanFlagType} from "../../plan/plan-flag-type";
import {PlannerCommandSplitLeg} from "../../commands/planner-command-split-leg";
import {PlannerDragLeg} from "../planner-drag-leg";
import {Observable} from "rxjs";
import {PlanLeg} from "../../plan/plan-leg";
import {map, switchMap} from "rxjs/operators";
import {FeatureId} from "../../features/feature-id";
import {PlanFlag} from "../../plan/plan-flag";

export class DropLegOnNode {

  constructor(private readonly context: PlannerContext) {
  }

  drop(legDrag: PlannerDragLeg, connection: PlanNode): void {
    const oldLeg = this.context.legs.getById(legDrag.oldLegId);
    if (oldLeg) {
      this.buildLeg1(oldLeg.sourceNode, connection).pipe(
        switchMap(newLeg1 =>
          this.buildLeg2(connection, oldLeg.sinkNode).pipe(
            map(newLeg2 => {
              return new PlannerCommandSplitLeg(oldLeg.featureId, newLeg1.featureId, newLeg2.featureId);
            })
          )
        )
      ).subscribe(command => this.context.execute(command))
    }
  }

  private buildLeg1(sourceNode: PlanNode, sinkNode: PlanNode): Observable<PlanLeg> {

    const source = PlanUtil.legEndNode(+sourceNode.nodeId);
    const sink = PlanUtil.legEndNode(+sinkNode.nodeId);

    return this.context.legRepository.planLeg(this.context.networkType, source, sink).pipe(
      map(data => {
        const legKey = PlanUtil.key(source, sink);
        const sinkFlag = PlanUtil.viaFlag(sinkNode.coordinate);
        const newLeg = new PlanLeg(FeatureId.next(), legKey, source, sink, sinkFlag, null, data.routes);
        this.context.legs.add(newLeg);
        return newLeg;
      })
    );
  }

  private buildLeg2(sourceNode: PlanNode, sinkNode: PlanNode): Observable<PlanLeg> {

    const source = PlanUtil.legEndNode(+sourceNode.nodeId);
    const sink = PlanUtil.legEndNode(+sinkNode.nodeId);
    const isLastLeg = sinkNode.featureId === this.context.plan.sinkNode().featureId;
    const sinkFlagType = isLastLeg ? PlanFlagType.End : PlanFlagType.Via;
    const sinkFlag = new PlanFlag(sinkFlagType, FeatureId.next(), sinkNode.coordinate);

    return this.context.legRepository.planLeg(this.context.networkType, source, sink).pipe(
      map(data => {
        const legKey = PlanUtil.key(source, sink);
        const newLeg = new PlanLeg(FeatureId.next(), legKey, source, sink, sinkFlag, null, data.routes);
        this.context.legs.add(newLeg);
        return newLeg;
      })
    );
  }

}
