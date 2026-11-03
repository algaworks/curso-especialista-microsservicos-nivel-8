import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-product-inventory-status-badge',
  templateUrl: './product-inventory-status-badge.component.html',
  styleUrl: './product-inventory-status-badge.component.css'
})
export class ProductInventoryStatusBadgeComponent {
  @Input()
  inventoryStatus!: boolean | undefined

  getStatusAsPrettyText() : string {
    if (this.inventoryStatus == true) {
      return 'In stock';
    }
    if (this.inventoryStatus == false) {
      return 'Out of stock';
    }
    return '';
  }

  getStatusColorClass() : string {
    if (this.inventoryStatus == true) {
      return 'status-success';
    }
    if (this.inventoryStatus == false) {
      return 'status-danger';
    }
    return '';
  }
}
