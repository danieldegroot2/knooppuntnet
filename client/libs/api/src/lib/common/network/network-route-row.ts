// this file is generated, please do not modify

import { Day } from '@api/custom';
import { Timestamp } from '@api/custom';

export interface NetworkRouteRow {
  readonly id: number;
  readonly name: string;
  readonly length: number;
  readonly role: string;
  readonly investigate: boolean;
  readonly accessible: boolean;
  readonly roleConnection: boolean;
  readonly lastUpdated: Timestamp;
  readonly lastSurvey: Day;
  readonly proposed: boolean;
  readonly symbol: string;
}
