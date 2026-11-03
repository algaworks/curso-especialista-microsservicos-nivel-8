import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuardWithForcedLogin } from './core/auth-guard-with-forced-login.service';
import { ForbiddenComponent } from './shared/error/forbidden/forbidden.component';
import { PageNotFoundComponent } from './shared/error/page-not-found/page-not-found.component';
import { LayoutComponent } from './shared/layout/layout.component';
import { PublicHomeComponent } from './public-home.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: PublicHomeComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivateChild: [AuthGuardWithForcedLogin],
    children: [{
      path: 'dashboard',
      loadChildren: () =>
          import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
    }, {
      path: 'users',
      loadChildren: () =>
          import('./features/users/users.module').then(m => m.UsersModule)
    }, {
      path: 'orders',
      loadChildren: () =>
          import('./features/orders/orders.module').then(m => m.OrdersModule)
    }, {
      path: 'customers',
      loadChildren: () =>
          import('./features/customers/customers.module').then(m => m.CustomersModule)
    }, {
      path: 'products',
      loadChildren: () =>
          import('./features/products/products.module').then(m => m.ProductsModule)
    }, {
      path: 'payments',
      loadChildren: () =>
          import('./features/payments/payments.module').then(m => m.PaymentsModule)
    }, {
      path: 'categories',
      loadChildren: () =>
          import('./features/categories/categories.module').then(m => m.CategoriesModule)
    }, {
      path: 'debug',
      loadChildren: () =>
          import('./feature-basics/basics.module').then(m => m.BasicsModule)
    }],
  },
 {
    path: 'not-authorize',
    component: ForbiddenComponent
  },
  {
    path: 'page-not-found',
    component: PageNotFoundComponent
  },
  {
    path: '**',
    redirectTo: 'page-not-found'
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
