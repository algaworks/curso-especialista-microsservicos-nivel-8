import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FooterComponent } from './layout/footer/footer.component';
import { HeaderComponent } from './layout/header/header.component';
import { MenuComponent } from './layout/menu/menu.component';
import { HeaderBreadcrumbComponent } from './layout/header-breadcrumb/header-breadcrumb.component';
import { LayoutComponent } from './layout/layout.component';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgPrimeModule } from '../app.ngprime.module';
import { PageNotFoundComponent } from './error/page-not-found/page-not-found.component';
import { ForbiddenComponent } from './error/forbidden/forbidden.component';
import { InternalComponent } from './error/internal/internal.component';
import { LabelWithDefaultComponent } from './helper/label-with-default/label-with-default.component';
import { RangeAsStringPipe } from './helper/range-as-string.pipe';
import { PrettyStatusFormatterComponent } from './helper/pretty-status-formatter/pretty-status-formatter.component';
import { ValidationMessageComponent } from './validation-message/validation-message.component';
import { AmountRangeAsStringPipe } from './helper/amount-range-as-string.pipe';
import { PrettyPaymentMethodNamePipePipe } from './helper/pretty-payment-method-name-pipe.pipe';
import { BlockCopyPasteDirective } from './helper/block-copy-paste.directive';

//import { NgxCurrencyModule } from 'ngx-currency';


@NgModule({
  declarations: [
    AmountRangeAsStringPipe,
    LayoutComponent,
    FooterComponent,
    HeaderComponent,
    MenuComponent,
    HeaderBreadcrumbComponent,
    FooterComponent,
    PageNotFoundComponent,
    ForbiddenComponent,
    InternalComponent,
    LabelWithDefaultComponent,
    RangeAsStringPipe,
    PrettyStatusFormatterComponent,
    ValidationMessageComponent,
    PrettyPaymentMethodNamePipePipe,
    BlockCopyPasteDirective
  ],
  imports: [
    RouterModule,
    CommonModule,
    NgPrimeModule,
    FormsModule,
    ReactiveFormsModule,
    //NgxCurrencyModule,
  ],
  exports: [
    NgPrimeModule,
    FormsModule,
    ReactiveFormsModule,
    RangeAsStringPipe,
    AmountRangeAsStringPipe,
    PrettyPaymentMethodNamePipePipe,
    LayoutComponent,
    FooterComponent,
    HeaderComponent,
    MenuComponent,
    HeaderBreadcrumbComponent,
    FooterComponent,
    LabelWithDefaultComponent,
    PrettyStatusFormatterComponent,
    ValidationMessageComponent,
    //NgxCurrencyModule,
    BlockCopyPasteDirective
  ]
})
export class SharedModule { }

export function sleep(ms: number = 120) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

export function enumToString(enumx:any, enumValue:any) : any {
  return Object.values(enumx)[Object.keys(enumx).indexOf(enumValue)]
}

export function stringToEnum(enumx:any, status:string) : any {
  return Object.keys(enumx)[Object.values(enumx).indexOf(status)]
}

export function enumToOptions(enumObj: any) {
  return Object.keys(enumObj).map(key => ({
      label: enumObj[key],
      value: key
  }));
}
