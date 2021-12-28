import { routerNavigatedAction } from '@ngrx/router-store';
import { createReducer } from '@ngrx/store';
import { on } from '@ngrx/store';
import { actionRouteChangesPageLoaded } from '../../analysis/route/store/route.actions';
import { actionRouteMapPageLoaded } from '../../analysis/route/store/route.actions';
import { actionRouteDetailsPageLoaded } from '../../analysis/route/store/route.actions';
import { Util } from '../../components/shared/util';
import { actionPreferencesAnalysisMode } from './preferences.actions';
import { actionPreferencesShowOptions } from './preferences.actions';
import { actionPreferencesShowLegend } from './preferences.actions';
import { actionPreferencesShowAppearanceOptions } from './preferences.actions';
import { actionPreferencesPlanProposed } from './preferences.actions';
import { actionPreferencesShowProposed } from './preferences.actions';
import { actionPreferencesImpact } from './preferences.actions';
import { actionPreferencesItemsPerPage } from './preferences.actions';
import { actionPreferencesNetworkType } from './preferences.actions';
import { actionPreferencesInstructions } from './preferences.actions';
import { actionPreferencesExtraLayers } from './preferences.actions';
import { AnalysisMode } from './preferences.state';
import { initialState } from './preferences.state';

export const preferencesReducer = createReducer(
  initialState,
  on(routerNavigatedAction, (state, action) => {
    if (action.payload.routerState.url.includes('/analysis/changes')) {
      const queryParams = action.payload.routerState.root.queryParams;
      const itemsPerPage = queryParams['itemsPerPage'];
      const impact = queryParams['impact'];
      let analysisMode: AnalysisMode;
      if ('network' === queryParams['network']) {
        analysisMode = AnalysisMode.network;
      } else if ('location' === queryParams['location']) {
        analysisMode = AnalysisMode.location;
      }
      return {
        ...state,
        analysisMode: analysisMode ?? state.analysisMode,
        itemsPerPage: itemsPerPage ?? state.itemsPerPage,
        impact: impact ?? state.impact,
      };
    } else {
      const params = Util.paramsIn(action.payload.routerState.root);
      const networkType = params.get('networkType');
      if (networkType) {
        return { ...state, networkType };
      }
      return state;
    }
  }),
  on(actionPreferencesAnalysisMode, (state, action) => ({
    ...state,
    analysisMode: action.analysisMode,
  })),
  on(actionPreferencesNetworkType, (state, action) => ({
    ...state,
    networkType: action.networkType,
  })),
  on(actionPreferencesInstructions, (state, action) => ({
    ...state,
    instructions: action.instructions,
  })),
  on(actionPreferencesExtraLayers, (state, action) => ({
    ...state,
    extraLayers: action.extraLayers,
  })),
  on(actionPreferencesItemsPerPage, (state, action) => ({
    ...state,
    itemsPerPage: action.itemsPerPage,
  })),
  on(actionPreferencesImpact, (state, action) => ({
    ...state,
    impact: action.impact,
  })),
  on(actionPreferencesShowAppearanceOptions, (state, action) => ({
    ...state,
    showAppearanceOptions: action.value,
  })),
  on(actionPreferencesShowLegend, (state, action) => ({
    ...state,
    showLegend: action.value,
  })),
  on(actionPreferencesShowOptions, (state, action) => ({
    ...state,
    showOptions: action.value,
  })),
  on(actionPreferencesShowProposed, (state, action) => ({
    ...state,
    showProposed: action.value,
  })),
  on(actionPreferencesPlanProposed, (state, action) => ({
    ...state,
    planProposed: action.value,
  })),
  on(actionRouteDetailsPageLoaded, (state, { response }) => {
    const networkType =
      response?.result?.route.summary.networkType ?? state.networkType;
    return {
      ...state,
      networkType,
    };
  }),
  on(actionRouteMapPageLoaded, (state, { response }) => {
    const networkType =
      response?.result?.routeMapInfo.networkType ?? state.networkType;
    return {
      ...state,
      networkType,
    };
  }),
  on(actionRouteChangesPageLoaded, (state, { response }) => {
    const networkType =
      response?.result?.routeNameInfo.networkType ?? state.networkType;
    return {
      ...state,
      networkType,
    };
  })
);
