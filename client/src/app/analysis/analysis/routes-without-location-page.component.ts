import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {List} from "immutable";
import {flatMap, map} from "rxjs/operators";
import {AppService} from "../../app.service";
import {Ref} from "../../kpn/api/common/common/ref";
import {Subscriptions} from "../../util/Subscriptions";

@Component({
  selector: "kpn-routes-without-location-page",
  template: `
    <div *ngIf="!!refs">
      No routes found
    </div>
    <div *ngIf="refs">
      <div *ngFor="let ref of refs; let i=index">
        <div class="nr">{{i + 1}}</div>
        <a class="text" [routerLink]="'/analysis/route/' + ref.id">{{ref.name}}</a>
      </div>
    </div>
  `,
  styles: [`
    .nr {
      display: inline-block;
      width: 50px;
    }
  `]
})
export class RoutesWithoutLocationPageComponent implements OnInit, OnDestroy {

  private readonly subscriptions = new Subscriptions();

  refs: List<Ref>;

  constructor(private activatedRoute: ActivatedRoute,
              private appService: AppService) {
  }

  ngOnInit() {
    this.subscriptions.add(
      this.activatedRoute.params.pipe(
        map(params => {
          return params["networkType"];
        }),
        flatMap(networkType => this.appService.location(networkType))
      ).subscribe(response => this.refs = response.result.routeRefs)
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

}
