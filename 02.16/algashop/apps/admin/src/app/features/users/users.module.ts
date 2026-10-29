import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UserListPageComponent } from './page/user-list-page/user-list-page.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { UserDetailPageComponent } from './page/user-detail-page/user-detail-page.component';
import { UserFormComponent } from './components/user-form/user-form.component';
import { UserEditPageComponent } from './page/user-edit-page/user-edit-page.component';
import { UserCreatePageComponent } from './page/user-create-page/user-create-page.component';
import { UserFilterComponent } from './components/user-filter/user-filter.component';


const routes: Routes = [
  {
    path: '',
    component: UserListPageComponent
  },
  {
    path: 'new',
    component: UserCreatePageComponent,
  },
  {
    path: ':userId',
    children: [
      {
        path: '',
        component: UserDetailPageComponent,
      },
      {
        path: 'edit',
        component: UserEditPageComponent,
      },
    ]
  }
];

@NgModule({
  declarations: [
    UserListPageComponent,
    UserDetailPageComponent,
    UserFormComponent,
    UserEditPageComponent,
    UserCreatePageComponent,
    UserFilterComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class UsersModule { }
