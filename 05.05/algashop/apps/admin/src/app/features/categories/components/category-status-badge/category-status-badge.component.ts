import { Component, Input } from '@angular/core';


@Component({
  selector: 'app-category-status-badge',
  templateUrl: './category-status-badge.component.html',
  styleUrl: './category-status-badge.component.css'
})
export class CategoryStatusBadgeComponent {
  @Input()
  enabled!: boolean | undefined

  getStatusAsPrettyText() : string {
    return this.enabled ? 'Active' : 'Inactive';
  }

  getStatusColorClass() : string {
    return this.enabled ? 'status-success' : 'status-warning';
  }
}
