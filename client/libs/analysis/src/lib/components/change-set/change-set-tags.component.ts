import { NgIf } from '@angular/common';
import { NgFor } from '@angular/common';
import { ChangeDetectionStrategy } from '@angular/core';
import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { Tag } from '@api/custom';
import { Tags } from '@api/custom';

@Component({
  selector: 'kpn-change-set-tags',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngIf="hasTags()">
      <div class="tags">
        <div *ngFor="let tag of tags()" class="tag">
          {{ tag.key }} = {{ tag.value }}
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .tags {
        padding-top: 5px;
        padding-left: 15px;
        padding-bottom: 25px;
      }

      .tag {
        padding-left: 5px;
        border-left-width: 1px;
        border-left-style: solid;
        border-left-color: #ccc;
        color: grey;
      }
    `,
  ],
  standalone: true,
  imports: [NgIf, NgFor],
})
export class ChangeSetTagsComponent {
  @Input() changeSetTags: Tags;

  tags(): Tag[] {
    if (this.changeSetTags && this.changeSetTags.tags) {
      return this.changeSetTags.tags.filter((tag) => tag.key !== 'comment');
    }
    return [];
  }

  hasTags(): boolean {
    return this.tags().length > 0;
  }
}
