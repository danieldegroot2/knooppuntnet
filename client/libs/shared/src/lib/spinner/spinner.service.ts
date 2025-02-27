import { Injectable } from '@angular/core';
import { List } from 'immutable';
import { finalize } from 'rxjs';
import { of } from 'rxjs';
import { Observable } from 'rxjs';
import { BehaviorSubject } from 'rxjs';
import { concatMap } from 'rxjs/operators';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class SpinnerService {
  readonly spinnerState$: Observable<boolean>;
  private readonly _spinnerState$ = new BehaviorSubject<boolean>(false);
  private activeActions: List<string> = List();

  constructor() {
    this.spinnerState$ = this._spinnerState$.asObservable();
  }

  showUntilCompleted<T>(obs$: Observable<T>, action: string): Observable<T> {
    return of(null).pipe(
      tap(() => this.start(action)),
      concatMap(() => obs$),
      finalize(() => this.end(action))
    );
  }

  start(action: string): void {
    this.activeActions = this.activeActions.push(action);
    if (this._spinnerState$.value !== true) {
      this._spinnerState$.next(true);
    }
    // console.log(`spinner start ${action} - activeActions = ${this.activeActions}, spinnerState=${this._spinnerState}`);
  }

  end(action: string): void {
    this.activeActions = this.activeActions.filter((a) => a !== action);
    if (this.activeActions.isEmpty() && this._spinnerState$.value !== false) {
      this._spinnerState$.next(false);
    }
    // console.log(`spinner end ${action} - activeActions = ${this.activeActions}, spinnerState=${this._spinnerState}`);
  }
}
