import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';
import { CategoryStatusBadgeComponent } from './components/category-status-badge/category-status-badge.component';
import { CategoryListPageComponent } from './page/category-list-page/category-list-page.component';
import { CategoryCreatePageComponent } from './page/category-create-page/category-create-page.component';
import { CategoryDetailPageComponent } from './page/category-detail-page/category-detail-page.component';
import { CategoryEditPageComponent } from './page/category-edit-page/category-edit-page.component';
import { CategoryFormComponent } from './components/category-form/category-form.component';
import { CategoryFilterComponent } from './components/category-filter/category-filter.component';

const routes: Routes = [
  {
    path: '',
    component: CategoryListPageComponent
  },
  {
    path: 'new',
    component: CategoryCreatePageComponent,
  },
  {
    path: ':categoryId',
    children: [
      {
        path: '',
        component: CategoryDetailPageComponent,
      },
      {
        path: 'edit',
        component: CategoryEditPageComponent,
      },
    ]
  }
];

@NgModule({
  declarations: [
    CategoryListPageComponent,
    CategoryDetailPageComponent,
    CategoryFormComponent,
    CategoryEditPageComponent,
    CategoryCreatePageComponent,
    CategoryStatusBadgeComponent,
    CategoryFilterComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class CategoriesModule { }
