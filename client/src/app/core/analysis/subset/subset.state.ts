import {SubsetChangesPage} from '../../../kpn/api/common/subset/subset-changes-page';
import {SubsetFactsPage} from '../../../kpn/api/common/subset/subset-facts-page';
import {SubsetMapPage} from '../../../kpn/api/common/subset/subset-map-page';
import {SubsetNetworksPage} from '../../../kpn/api/common/subset/subset-networks-page';
import {SubsetOrphanNodesPage} from '../../../kpn/api/common/subset/subset-orphan-nodes-page';
import {SubsetOrphanRoutesPage} from '../../../kpn/api/common/subset/subset-orphan-routes-page';
import {ApiResponse} from '../../../kpn/api/custom/api-response';

export const initialState: SubsetState = {
  networks: null,
  facts: null,
  orphanNodes: null,
  orphanRoutes: null,
  map: null,
  changes: null
};

export interface SubsetState {
  networks: ApiResponse<SubsetNetworksPage>;
  facts: ApiResponse<SubsetFactsPage>;
  orphanNodes: ApiResponse<SubsetOrphanNodesPage>;
  orphanRoutes: ApiResponse<SubsetOrphanRoutesPage>;
  map: ApiResponse<SubsetMapPage>;
  changes: ApiResponse<SubsetChangesPage>;
}
