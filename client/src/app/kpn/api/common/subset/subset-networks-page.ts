// this class is generated, please do not modify

import {List} from "immutable";
import {NetworkAttributes} from "../network/network-attributes";
import {SubsetInfo} from "./subset-info";

export class SubsetNetworksPage {

  constructor(readonly subsetInfo: SubsetInfo,
              readonly km: string,
              readonly networkCount: number,
              readonly nodeCount: number,
              readonly routeCount: number,
              readonly brokenRouteNetworkCount: number,
              readonly brokenRouteNetworkPercentage: string,
              readonly brokenRouteCount: number,
              readonly brokenRoutePercentage: string,
              readonly unaccessibleRouteCount: number,
              readonly analysisUpdatedTime: string,
              readonly networks: List<NetworkAttributes>) {
  }

  public static fromJSON(jsonObject): SubsetNetworksPage {
    if (!jsonObject) {
      return undefined;
    }
    return new SubsetNetworksPage(
      SubsetInfo.fromJSON(jsonObject.subsetInfo),
      jsonObject.km,
      jsonObject.networkCount,
      jsonObject.nodeCount,
      jsonObject.routeCount,
      jsonObject.brokenRouteNetworkCount,
      jsonObject.brokenRouteNetworkPercentage,
      jsonObject.brokenRouteCount,
      jsonObject.brokenRoutePercentage,
      jsonObject.unaccessibleRouteCount,
      jsonObject.analysisUpdatedTime,
      jsonObject.networks ? List(jsonObject.networks.map(json => NetworkAttributes.fromJSON(json))) : List()
    );
  }
}
