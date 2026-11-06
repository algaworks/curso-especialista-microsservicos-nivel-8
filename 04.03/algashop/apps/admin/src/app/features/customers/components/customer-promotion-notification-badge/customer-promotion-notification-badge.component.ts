import { Component, Input } from '@angular/core';


@Component({
  selector: 'app-customer-promotion-notification-badge',
  templateUrl: './customer-promotion-notification-badge.component.html',
  styleUrl: './customer-promotion-notification-badge.component.css'
})
export class CustomerPromotionNotificationBadgeComponent {
  @Input()
  allowPromotionNotifications!: boolean | undefined

  getStatusAsPrettyText() : string {
    return this.allowPromotionNotifications ? 'Allowed' : 'Not allow';
  }

  getStatusColorClass() : string {
    return this.allowPromotionNotifications ? 'status-success' : 'status-warning';
  }
}
