// this file is generated, please do not modify

import { Node } from './node';
import { RawWay } from './raw';

export interface Way {
  readonly raw: RawWay;
  readonly nodes: Node[];
  readonly length: number;
}
