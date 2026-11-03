import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { DashboardDetailPageComponent } from './page/dashboard-detail-page.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { OrderStatus } from '../orders/models/model';
import { OrderStatusBadgeComponent } from '../orders/components/order-status-badge/order-status-badge.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardDetailPageComponent
  },
];

@NgModule({
  declarations: [
    DashboardDetailPageComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class DashboardModule { }
