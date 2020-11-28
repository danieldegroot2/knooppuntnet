import {Injectable} from '@angular/core';
import {Actions} from '@ngrx/effects';
import {createEffect} from '@ngrx/effects';
import {ofType} from '@ngrx/effects';
import {routerNavigatedAction} from '@ngrx/router-store';
import {Store} from '@ngrx/store';
import {withLatestFrom} from 'rxjs/operators';
import {map} from 'rxjs/operators';
import {filter} from 'rxjs/operators';
import {mergeMap} from 'rxjs/operators';
import {AppService} from '../../../app.service';
import {selectUrl} from '../../core.state';
import {selectRouteParams} from '../../core.state';
import {AppState} from '../../core.state';
import {actionNodeMapLoaded} from './node.actions';
import {actionNodeDetailsLoaded} from './node.actions';

@Injectable()
export class NodeEffects {
  constructor(private actions$: Actions,
              private store: Store<AppState>,
              private appService: AppService) {
  }

  nodePageEnter = createEffect(() =>
    this.actions$.pipe(
      ofType(routerNavigatedAction),
      withLatestFrom(
        this.store.select(selectUrl),
        this.store.select(selectRouteParams)
      ),
      filter(([action, url, params]) => url.startsWith('/analysis/node/')),
      mergeMap(([action, url, params]) => {
        const nodeId = params['nodeId'];
        if (url.endsWith('/map')) {
          return this.appService.nodeMap(nodeId).pipe(
            map(response => actionNodeMapLoaded({response}))
          );
        }
        // if (url.endsWith('/changes')) {
        //   return this.appService.nodeChanges(nodeId, null /* TODO PARAMETERS */).pipe(
        //     map(response => actionNodeChangesLoaded({response}))
        //   );
        // }
        return this.appService.nodeDetails(nodeId).pipe(
          map(response => actionNodeDetailsLoaded({response}))
        );
      })
    )
  );

}
