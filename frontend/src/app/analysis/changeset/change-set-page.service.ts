import { signal } from '@angular/core';
import { inject } from '@angular/core';
import { Params } from '@angular/router';
import { ChangeSetPage } from '@api/common/changes';
import { ApiResponse } from '@api/custom';
import { Util } from '@app/components/shared';
import { ApiService } from '@app/services';
import { RouterService } from '../../shared/services/router.service';

class ChangeSetKey {
  constructor(
    readonly changeSetId: string,
    readonly replicationNumber: string
  ) {}
}

export class ChangeSetPageService {
  private readonly apiService = inject(ApiService);
  private readonly routerService = inject(RouterService);
  private readonly key = this.interpretParams(this.routerService.params());
  private _response = signal<ApiResponse<ChangeSetPage>>(null);
  readonly response = this._response.asReadonly();
  readonly changeSetTitle = this.initChangeSetTitle();

  onInit(): void {
    const key = this.interpretParams(this.routerService.params());
    this.apiService
      .changeSet(key.changeSetId, key.replicationNumber)
      .subscribe((response) => this._response.set(response));
  }

  private interpretParams(params: Params): ChangeSetKey {
    const changeSetId = params['changeSetId'];
    const replicationNumber = params['replicationNumber'];
    return new ChangeSetKey(changeSetId, replicationNumber);
  }

  private initChangeSetTitle(): string {
    return this.key.changeSetId + ' ' + Util.replicationName(+this.key.replicationNumber);
  }
}
