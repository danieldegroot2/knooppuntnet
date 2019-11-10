import {Component, Input} from "@angular/core";
import {List} from "immutable";
import {NetworkType} from "../../../kpn/api/custom/network-type";
import {RouteMemberInfo} from "../../../kpn/api/custom/route-member-info";

@Component({
  selector: "kpn-route-members",
  template: `
    <div>
      <h4 i18n="@@route.members.title">Route Members</h4>
      <div *ngIf="members.isEmpty()">
        <span i18n="@@route.members.none">None</span>
      </div>
      <div *ngIf="!members.isEmpty()">

        <table class="kpn-table">
          <thead>
          <tr>
            <th></th>
            <th i18n="@@route.members.table.node">Node</th>
            <th i18n="@@route.members.table.id">Id</th>
            <th colSpan="2" i18n="@@route.members.table.nodes">Nodes</th>
            <th i18n="@@route.members.table.role">Role</th>
            <th i18n="@@route.members.table.length">Length</th>
            <th i18n="@@route.members.table.node-count">#Nodes</th>
            <th i18n="@@route.members.table.name">Name</th>
            <th i18n="@@route.members.table.unaccessible">Unaccessible</th>
            <th colSpan="2" *ngIf="networkType.name == 'cycling'" i18n="@@route.members.table.one-way">One Way</th>
          </tr>
          </thead>

          <tbody>
          <tr *ngFor="let member of members">

            <td class="image-cell">
              <div class="image">
                <img [src]="'/assets/images/links/' + member.linkName + '.png'" [alt]="member.linkName">
              </div>
            </td>
            <td>
              <div class="kpn-comma-list">
                <kpn-link-node
                  *ngFor="let node of member.nodes"
                  [nodeId]="node.id"
                  [nodeName]="node.alternateName">
                </kpn-link-node>
              </div>
            </td>
            <td>
              <kpn-osm-link kind="{{member.memberType}}" id="{{member.id}}" title="{{member.id}}"></kpn-osm-link>
            </td>
            <td>
              <kpn-osm-link kind="node" id="{{member.fromNodeId}}" title="{{member.from}}"></kpn-osm-link>
            </td>
            <td>
              <kpn-osm-link *ngIf="member.isWay" kind="node" id="{{member.toNodeId}}" title="{{member.to}}"></kpn-osm-link>
            </td>
            <td>
              {{member.role}}
            </td>
            <td>
              {{member.length}}
            </td>
            <td>
              {{member.nodeCount}}
            </td>
            <td>
              {{member.description}}
            </td>
            <td>
              <div *ngIf="!member.isAccessible">
                <mat-icon svgIcon="warning"></mat-icon>
              </div>
            </td>
            <td *ngIf="networkType.name == 'cycling'">
              <div *ngIf="member.oneWay == 'Forward'" i18n="@@route.members.table.one-way.yes">Yes</div>
              <div *ngIf="member.oneWay == 'Backward'" i18n="@@route.members.table.one-way.reverse">Reverse</div> 
            </td>
            <td>
              <kpn-tags-text *ngIf="!member.oneWayTags.isEmpty()" [tags]="member.oneWayTags"></kpn-tags-text>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .image-cell {
      padding: 0;
      height: 40px;
      min-height: 40px;
      max-height: 40px;
    }
    .image {
      height: 40px;
      padding: 0;
      margin: 0;
    }
  `]
})
export class RouteMembersComponent {
  @Input() networkType: NetworkType;
  @Input() members: List<RouteMemberInfo>;
}
