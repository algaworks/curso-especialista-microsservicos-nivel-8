import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductListPageComponent } from './page/product-list-page/product-list-page.component';
import { ProductDetailPageComponent } from './page/product-detail-page/product-detail-page.component';
import { RouterModule, Routes } from '@angular/router';
import { ProductFormComponent } from './components/product-form/product-form.component';
import { ProductEditPageComponent } from './page/product-edit-page/product-edit-page.component';
import { ProductCreatePageComponent } from './page/product-create-page/product-create-page.component';
import { ProductStatusBadgeComponent } from './components/product-status-badge/product-status-badge.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ProductInventoryStatusBadgeComponent } from './components/product-inventory-status-badge/product-inventory-status-badge.component';
import { ProductAddImageFormComponent } from './components/product-add-image-form/product-add-image-form.component';
import { ProductFilterComponent } from './components/product-filter/product-filter.component';

const routes: Routes = [
  {
    path: '',
    component: ProductListPageComponent
  },
  { 
    path: 'new', 
    component: ProductCreatePageComponent,
  },
  {
    path: ':productId',
    children: [
      {
        path: '', 
        component: ProductDetailPageComponent,
      },
      {
        path: 'edit', 
        component: ProductEditPageComponent,
      },
    ]
  }
];

@NgModule({
  declarations: [
    ProductListPageComponent,
    ProductDetailPageComponent,
    ProductFormComponent,
    ProductEditPageComponent,
    ProductCreatePageComponent,
    ProductStatusBadgeComponent,
    ProductInventoryStatusBadgeComponent,
    ProductAddImageFormComponent,
    ProductFilterComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class ProductsModule { }
