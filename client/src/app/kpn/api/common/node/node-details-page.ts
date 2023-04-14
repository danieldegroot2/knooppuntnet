// this file is generated, please do not modify

import { Reference } from '@api/common/common';
import { NodeInfo } from '../node-info';
import { NodeIntegrity } from './node-integrity';

export interface NodeDetailsPage {
  readonly nodeInfo: NodeInfo;
  readonly mixedNetworkScopes: boolean;
  readonly routeReferences: Reference[];
  readonly networkReferences: Reference[];
  readonly integrity: NodeIntegrity;
  readonly changeCount: number;
}
