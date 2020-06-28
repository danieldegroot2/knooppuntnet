import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {AppService} from "../../../app.service";
import {LegBuildParams} from "../../../kpn/api/common/planner/leg-build-params";
import {LegEnd} from "../../../kpn/api/common/planner/leg-end";
import {RouteLeg} from "../../../kpn/api/common/planner/route-leg";
import {NetworkType} from "../../../kpn/api/custom/network-type";
import {PlannerLegRepository} from "./planner-leg-repository";

export class PlannerLegRepositoryImpl implements PlannerLegRepository {

  constructor(private appService: AppService) {
  }

  planLeg(networkType: NetworkType, legId: string, source: LegEnd, sink: LegEnd): Observable<RouteLeg> {
    const params = new LegBuildParams(networkType.name, legId, source, sink);
    return this.appService.routeLeg(params).pipe(
      map(response => response.result)
    );
  }
}
