import { routerNavigationAction } from '@ngrx/router-store';
import { createReducer } from '@ngrx/store';
import { on } from '@ngrx/store';
import { actionNodeMapPageLoad } from './node.actions';
import { actionNodeDetailsPageLoad } from './node.actions';
import { actionNodeChangesPageLoad } from './node.actions';
import { actionNodeChangesPageImpact } from './node.actions';
import { actionNodeChangesPageSize } from './node.actions';
import { actionNodeChangesPageIndex } from './node.actions';
import { actionNodeChangesFilterOption } from './node.actions';
import { actionNodeDetailsPageLoaded } from './node.actions';
import { actionNodeMapPageLoaded } from './node.actions';
import { actionNodeChangesPageLoaded } from './node.actions';
import { NodeState } from './node.state';
import { initialState } from './node.state';

export const nodeReducer = createReducer<NodeState>(
  initialState,
  on(
    routerNavigationAction,
    (state): NodeState => ({
      ...state,
      detailsPage: null,
      mapPage: null,
      mapPositionFromUrl: null,
      changesPage: null,
    })
  ),
  on(actionNodeDetailsPageLoad, (state, { nodeId, nodeName }): NodeState => {
    return {
      ...state,
      nodeId,
      nodeName: nodeName ? nodeName : state.nodeName,
    };
  }),
  on(
    actionNodeMapPageLoad,
    (state, { nodeId }): NodeState => ({
      ...state,
      nodeId,
    })
  ),
  on(actionNodeDetailsPageLoaded, (state, response): NodeState => {
    const nodeId = response.result?.nodeInfo.id.toString() ?? state.nodeId;
    const nodeName = response.result?.nodeInfo.name ?? state.nodeName;
    const changeCount = response.result?.changeCount ?? state.changeCount;
    return {
      ...state,
      nodeId,
      nodeName,
      changeCount,
      detailsPage: response,
    };
  }),
  on(
    actionNodeMapPageLoaded,
    (state, { response, mapPositionFromUrl }): NodeState => {
      const nodeId = response.result?.nodeMapInfo.id.toString() ?? state.nodeId;
      const nodeName = response.result?.nodeMapInfo.name ?? state.nodeName;
      const changeCount = response.result?.changeCount ?? state.changeCount;
      return {
        ...state,
        nodeId,
        nodeName,
        changeCount,
        mapPage: response,
        mapPositionFromUrl,
      };
    }
  ),
  on(
    actionNodeChangesPageLoad,
    (state, { nodeId, changesParameters }): NodeState => ({
      ...state,
      nodeId,
      changesParameters,
    })
  ),
  on(actionNodeChangesPageLoaded, (state, response): NodeState => {
    const nodeId = response.result?.nodeId.toString() ?? state.nodeId;
    const nodeName = response.result?.nodeName ?? state.nodeName;
    const changeCount = response.result?.changeCount ?? state.changeCount;
    return {
      ...state,
      nodeId,
      nodeName,
      changeCount,
      changesPage: response,
    };
  }),
  on(
    actionNodeChangesPageImpact,
    (state, action): NodeState => ({
      ...state,
      changesParameters: {
        ...state.changesParameters,
        impact: action.impact,
        pageIndex: 0,
      },
    })
  ),
  on(
    actionNodeChangesPageSize,
    (state, action): NodeState => ({
      ...state,
      changesParameters: {
        ...state.changesParameters,
        pageSize: action.pageSize,
        pageIndex: 0,
      },
    })
  ),
  on(
    actionNodeChangesPageIndex,
    (state, action): NodeState => ({
      ...state,
      changesParameters: {
        ...state.changesParameters,
        pageIndex: action.pageIndex,
      },
    })
  ),
  on(
    actionNodeChangesFilterOption,
    (state, action): NodeState => ({
      ...state,
      changesParameters: {
        ...state.changesParameters,
        year: action.option.year,
        month: action.option.month,
        day: action.option.day,
        impact: action.option.impact,
        pageIndex: 0,
      },
    })
  )
);
