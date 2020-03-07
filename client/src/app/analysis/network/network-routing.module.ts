import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {AnalysisSidebarComponent} from "../../components/shared/sidebar/analysis-sidebar.component";
import {Util} from "../../components/shared/util";
import {NetworkChangesPageComponent} from "./changes/_network-changes-page.component";
import {NetworkChangesSidebarComponent} from "./changes/network-changes-sidebar.component";
import {NetworkDetailsPageComponent} from "./details/_network-details-page.component";
import {NetworkFactsPageComponent} from "./facts/_network-facts-page.component";
import {NetworkMapPageComponent} from "./map/_network-map-page.component";
import {NetworkNodesPageComponent} from "./nodes/_network-nodes-page.component";
import {NetworkNodesSidebarComponent} from "./nodes/network-nodes-sidebar.component";
import {NetworkRoutesPageComponent} from "./routes/_network-routes-page.component";
import {NetworkRoutesSidebarComponent} from "./routes/network-routes-sidebar.component";

const routes: Routes = [
  Util.routePath(":networkId", NetworkDetailsPageComponent, AnalysisSidebarComponent),
  Util.routePath(":networkId/facts", NetworkFactsPageComponent, AnalysisSidebarComponent),
  Util.routePath(":networkId/nodes", NetworkNodesPageComponent, NetworkNodesSidebarComponent),
  Util.routePath(":networkId/routes", NetworkRoutesPageComponent, NetworkRoutesSidebarComponent),
  Util.routePath(":networkId/map", NetworkMapPageComponent, AnalysisSidebarComponent),
  Util.routePath(":networkId/changes", NetworkChangesPageComponent, NetworkChangesSidebarComponent)
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class NetworkRoutingModule {
}
