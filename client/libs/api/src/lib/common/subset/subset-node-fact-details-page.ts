// this file is generated, please do not modify

import { Fact } from '@api/custom';
import { NetworkFactRefs } from './network-fact-refs';
import { SubsetInfo } from './subset-info';

export interface SubsetNodeFactDetailsPage {
  readonly subsetInfo: SubsetInfo;
  readonly fact: Fact;
  readonly networks: NetworkFactRefs[];
}
