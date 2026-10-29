import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CustomerListPageComponent } from './page/customer-list-page/customer-list-page.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CustomerDetailPageComponent } from './page/customer-detail-page/customer-detail-page.component';
import { CustomerFilterComponent } from './components/customer-filter/customer-filter.component';
import { CustomerPromotionNotificationBadgeComponent } from './components/customer-promotion-notification-badge/customer-promotion-notification-badge.component';


const routes: Routes = [
  {
    path: '',
    component: CustomerListPageComponent
  },
  {
    path: ':customerId',
    children: [
      {
        path: '',
        component: CustomerDetailPageComponent,
      },
    ]
  }
];

@NgModule({
  declarations: [
    CustomerListPageComponent,
    CustomerDetailPageComponent,
    CustomerFilterComponent,
    CustomerPromotionNotificationBadgeComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class CustomersModule { }
