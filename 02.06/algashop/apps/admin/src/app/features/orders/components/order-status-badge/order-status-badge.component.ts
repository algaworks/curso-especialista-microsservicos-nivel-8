import { Component, Input } from '@angular/core';
import { OrderStatus } from '../../models/model';
import { enumToString } from 'src/app/shared/shared.module';


@Component({
  selector: 'app-order-status-badge',
  templateUrl: './order-status-badge.component.html',
  styleUrl: './order-status-badge.component.css'
})
export class OrderStatusBadgeComponent {
  @Input()
  status!: OrderStatus | undefined

  getStatusAsPrettyText() : string {
    return enumToString(OrderStatus, this.status);
  }

  getStatusColorClass() : string {
    switch (enumToString(OrderStatus, this.status!)) {
      case OrderStatus.PAID:
        return 'status-success';
      case OrderStatus.CANCELED:
        return 'status-danger';
      case OrderStatus.READY:
        return 'status-success';
      default:
        return 'status-info';
    }
  }
}
