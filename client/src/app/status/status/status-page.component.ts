import {OnInit} from "@angular/core";
import {Component} from "@angular/core";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";
import {AppService} from "../../app.service";
import {Status} from "../../kpn/api/common/status/status";
import {ApiResponse} from "../../kpn/api/custom/api-response";
import {StatusLinks} from "./status-links";

/* tslint:disable:template-i18n English only */
@Component({
  selector: "kpn-status-page",
  template: `
    <div *ngIf="response$ | async as response">
      <p>
        <span class="kpn-label" i18n="@@status.situation-on">The analysis is up-to-date until</span>
        <kpn-timestamp [timestamp]="response.situationOn"></kpn-timestamp>
      </p>
      <p>
        <span class="kpn-label" i18n="@@status.replication">Replication details</span>
        <kpn-status-links [links]="replicationLinks"></kpn-status-links>
      </p>
      <p>
        <span class="kpn-label" i18n="@@status.system">System details</span>
        <kpn-status-links [links]="systemLinks"></kpn-status-links>
      </p>
    </div>
  `
})
export class StatusPageComponent implements OnInit {

  response$: Observable<ApiResponse<Status>>;

  replicationLinks: StatusLinks;
  systemLinks: StatusLinks;

  constructor(private readonly appService: AppService) {
  }

  ngOnInit(): void {
    this.response$ = this.appService.status().pipe(
      tap(response => {
        const timestamp = response.result.timestamp;
        this.replicationLinks = new StatusLinks(timestamp, "/status/replication");
        this.systemLinks = new StatusLinks(timestamp, "/status/system");
      })
    );
  }

}
