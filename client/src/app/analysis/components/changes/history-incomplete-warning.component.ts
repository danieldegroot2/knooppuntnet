import {Component} from "@angular/core";

@Component({
  selector: "kpn-history-incomplete-warning",
  template: `
    <p class="note" i18n="@@history-incomplete.warning">
      Older changes cannot be shown. The history in the analysis database
      does not go beyond 2012-09-12 (
      <a class="external" href="https://wiki.openstreetmap.org/wiki/NL:Open_Database_License" target="_blank">
        License change
      </a>).
    </p>
  `
})
export class HistoryIncompleteWarningComponent {
}
