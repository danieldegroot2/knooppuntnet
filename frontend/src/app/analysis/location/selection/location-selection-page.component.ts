import { AsyncPipe } from '@angular/common';
import { inject } from '@angular/core';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { LocationNode } from '@api/common/location';
import { Country } from '@api/custom';
import { NetworkType } from '@api/custom';
import { CountryNameComponent } from '@app/components/shared';
import { NetworkTypeNameComponent } from '@app/components/shared';
import { ErrorComponent } from '@app/components/shared/error';
import { PageComponent } from '@app/components/shared/page';
import { PageHeaderComponent } from '@app/components/shared/page';
import { Countries } from '@app/kpn/common';
import { NetworkTypes } from '@app/kpn/common';
import { Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { map } from 'rxjs/operators';
import { LocalLocationNode } from './local-location-node';
import { LocationModeService } from './location-mode.service';
import { LocationSelectionSidebarComponent } from './location-selection-sidebar.component';
import { LocationSelectionService } from './location-selection.service';
import { LocationSelectorComponent } from './location-selector.component';
import { LocationTreeComponent } from './location-tree.component';

@Component({
  selector: 'kpn-location-selection-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <kpn-page>
      <kpn-error />

      @if (locationNode$ | async; as locationNode) {
        <div>
          <ul class="breadcrumb">
            <li><a routerLink="/" i18n="@@breadcrumb.home">Home</a></li>
            <li>
              <a routerLink="/analysis" i18n="@@breadcrumb.analysis">Analysis</a>
            </li>
            <li>
              <a [routerLink]="networkTypeLink()">
                <kpn-network-type-name [networkType]="networkType" />
              </a>
            </li>
            <li>
              <kpn-country-name [country]="country" />
            </li>
          </ul>
          <kpn-page-header [pageTitle]="'Locations'" subject="network-page">
            <span class="header-network-type-icon">
              <mat-icon [svgIcon]="networkType" />
            </span>
            <kpn-network-type-name [networkType]="networkType" />
            <span i18n="@@subset.in" class="in">in</span>
            <kpn-country-name [country]="country" />
          </kpn-page-header>
          @if (isModeName() | async) {
            <div>
              <kpn-location-selector
                [country]="country"
                [locationNode]="locationNode"
                (selection)="selected($event)"
              />
            </div>
          }
          @if (isModeTree() | async) {
            <div>
              <kpn-location-tree
                [networkType]="networkType"
                [country]="country"
                [locationNode]="locationNode"
                (selection)="selected($event)"
              />
            </div>
          }
        </div>
      }
      <kpn-location-selection-sidebar sidebar />
    </kpn-page>
  `,
  styles: `
    .in:before {
      content: ' ';
    }

    .in:after {
      content: ' ';
    }
  `,
  standalone: true,
  imports: [
    AsyncPipe,
    CountryNameComponent,
    ErrorComponent,
    LocationSelectionSidebarComponent,
    LocationSelectorComponent,
    LocationTreeComponent,
    MatIconModule,
    NetworkTypeNameComponent,
    PageComponent,
    PageHeaderComponent,
    RouterLink,
  ],
})
export class LocationSelectionPageComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly locationModeService = inject(LocationModeService);
  private readonly locationSelectionService = inject(LocationSelectionService);
  private readonly router = inject(Router);

  protected locationNode$: Observable<LocalLocationNode>;
  protected networkType: NetworkType;
  protected country: Country;

  isModeName() {
    return this.locationModeService.isModeName;
  }

  isModeTree() {
    return this.locationModeService.isModeTree;
  }

  selected(locationName: string): void {
    const url = `/analysis/${this.networkType}/${this.country}/${locationName}/nodes`;
    this.router.navigateByUrl(url);
  }

  ngOnInit() {
    // TODO this.store.dispatch(actionLocationSelectionPageInit());

    this.locationNode$ = this.activatedRoute.params.pipe(
      map((params) => {
        this.networkType = NetworkTypes.withName(params['networkType']);
        this.country = Countries.withDomain(params['country']);
        return { country: this.country, networkType: this.networkType };
      }),
      mergeMap((subset) =>
        this.locationSelectionService.locations(subset.networkType, subset.country)
      ),
      map((locationNode) => this.toLocalLocationNode([], locationNode))
    );
  }

  // ngOnDestroy(): void {
  //   this.store.dispatch(actionLocationSelectionPageDestroy());
  // }

  private toLocalLocationNode(
    parents: LocationNode[],
    locationNode: LocationNode
  ): LocalLocationNode {
    const localPath = parents.map((ln) => ln.name).join(':');
    const childParents: LocationNode[] = [];
    parents.forEach((parent) => childParents.push(parent));
    childParents.push(locationNode);

    let localChildren: LocalLocationNode[] = [];
    if (locationNode.children) {
      localChildren = locationNode.children.map((child) =>
        this.toLocalLocationNode(childParents, child)
      );
    }

    return {
      path: localPath,
      name: locationNode.name,
      nodeCount: locationNode.nodeCount,
      children: localChildren,
    };
  }

  networkTypeLink(): string {
    return `/analysis/${this.networkType}`;
  }
}
