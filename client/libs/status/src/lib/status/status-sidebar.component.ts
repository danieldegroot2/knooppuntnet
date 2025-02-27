import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { SidebarComponent } from '@app/components/shared/sidebar';

@Component({
  selector: 'kpn-status-sidebar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: '<kpn-sidebar/>',
  standalone: true,
  imports: [SidebarComponent],
})
export class StatusSidebarComponent {}
