import { HttpErrorResponse } from '@angular/common/http';
import { HttpEvent } from '@angular/common/http';
import { HttpHandler } from '@angular/common/http';
import { HttpInterceptor } from '@angular/common/http';
import { HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { actionSharedHttpError } from '@app/core';
import { Store } from '@ngrx/store';
import { throwError } from 'rxjs';
import { of } from 'rxjs';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SpinnerService } from './spinner.service';

@Injectable()
export class SpinnerInterceptor implements HttpInterceptor {
  constructor(private spinnerService: SpinnerService, private store: Store) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const result$ = next.handle(request).pipe(
      catchError((error) => {
        let httpError = 'error';
        if (error instanceof HttpErrorResponse) {
          if (error.error instanceof ErrorEvent) {
            httpError = 'error-event';
          } else {
            httpError = 'error-' + error.status;
          }
        }
        if (
          request.url.includes(
            'import?url=https://api.openstreetmap.org/api/0.6'
          )
        ) {
          return throwError(error);
        }
        this.store.dispatch(actionSharedHttpError({ httpError }));
        return of(null);
      })
    );

    return this.spinnerService.showUntilCompleted(result$, 'http-request');
  }
}
