import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { OrderListPageComponent } from './page/order-list-page/order-list-page.component';
import { OrderDetailPageComponent } from './page/order-detail-page/order-detail-page.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderFilterComponent } from './components/order-filter/order-filter.component';
import { OrderStatusBadgeComponent } from './components/order-status-badge/order-status-badge.component';


const routes: Routes = [
  {
    path: '',
    component: OrderListPageComponent
  },
  {
    path: ':orderCode',
    component: OrderDetailPageComponent,
  }
];

@NgModule({
  declarations: [
    OrderListPageComponent,
    OrderDetailPageComponent,
    OrderFilterComponent,
    OrderStatusBadgeComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class OrdersModule { }
