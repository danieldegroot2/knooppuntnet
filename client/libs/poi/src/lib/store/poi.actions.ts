import { LocationPoiSummaryPage } from '@api/common/poi';
import { LocationPoisPage } from '@api/common/poi';
import { PoiLocationsPage } from '@api/common/poi';
import { ApiResponse } from '@api/custom';
import { Country } from '@api/custom';
import { createAction } from '@ngrx/store';
import { props } from '@ngrx/store';

export const actionLocationPoisPageInit = createAction(
  '[LocationPoisPage] Init'
);

export const actionLocationPoisPageLoaded = createAction(
  '[LocationPoisPage] Loaded',
  props<ApiResponse<LocationPoisPage>>()
);

export const actionLocationPoisPageIndex = createAction(
  '[LocationPoisPage] Page index',
  props<{ pageIndex: number }>()
);

export const actionLocationPoiSummaryPageInit = createAction(
  '[LocationPoiSummaryPage] Init'
);

export const actionLocationPoiSummaryPageLoaded = createAction(
  '[LocationPoiSummaryPage] Loaded',
  props<ApiResponse<LocationPoiSummaryPage>>()
);

export const actionLocationPoiSummaryCountryChanged = createAction(
  '[LocationPoiSummaryPage] Country changed',
  props<{ country: Country }>()
);

export const actionLocationPoiSummaryLocationsLoaded = createAction(
  '[LocationPoiSummaryPage] Locations loaded',
  props<ApiResponse<PoiLocationsPage>>()
);

export const actionPoiAreasPageInit = createAction('[PoiAreasPage] Init');

export const actionPoiAreasPageLoaded = createAction(
  '[PoiAreasPage] Loaded',
  props<ApiResponse<string>>()
);

export const actionPoiAreasPageMapViewInit = createAction(
  '[PoiAreasPage] View init'
);
