import {Component, Input} from "@angular/core";
import {NodeDiffsData} from "./node-diffs-data";

@Component({
  selector: "kpn-node-diffs",
  template: `
    <kpn-node-diffs-removed [data]="data"></kpn-node-diffs-removed>
    <kpn-node-diffs-added [data]="data"></kpn-node-diffs-added>
    <kpn-node-diffs-updated [data]="data"></kpn-node-diffs-updated>
  `
})
export class NodeDiffsComponent {
  @Input() data: NodeDiffsData;
}
