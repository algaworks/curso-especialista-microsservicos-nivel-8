import { Component, Input } from '@angular/core';


@Component({
  selector: 'app-product-status-badge',
  templateUrl: './product-status-badge.component.html',
  styleUrl: './product-status-badge.component.css'
})
export class ProductStatusBadgeComponent {
  @Input()
  enabled!: boolean | undefined

  getStatusAsPrettyText() : string {
    return this.enabled ? 'Active' : 'Inactive';
  }

  getStatusColorClass() : string {
    return this.enabled ? 'status-success' : 'status-warning';
  }
}
