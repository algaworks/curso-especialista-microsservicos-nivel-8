import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';

//import { AppMenuComponent } from './app-menu.component';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { FallbackComponent } from './fallback.component';
import { PublicHomeComponent } from './public-home.component';
import { ShouldLoginComponent } from './should-login.component';

import localeUs from '@angular/common/locales/en';
import { registerLocaleData } from '@angular/common';
import { SharedModule } from './shared/shared.module';
import { MessageService, ConfirmationService } from 'primeng/api';
import { DialogService, DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { AppRoutingModule } from './app-routing.module';

registerLocaleData(localeUs);

export const locale = 'en';

export const localeDateTimeFormat : any = {
  year: 'numeric',
  month: 'numeric',
  day: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
}

@NgModule({
  declarations: [
    AppComponent,
    //AppMenuComponent,
    FallbackComponent,
    PublicHomeComponent,
    ShouldLoginComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule,
    AppRoutingModule,
    CoreModule.forRoot(),
    SharedModule,
    //RouterModule.forRoot([
    //{ path: '', redirectTo: 'basics/home', pathMatch: 'full' },
    //// Note: this way of module loading requires this in your tsconfig.json: "module": "esnext"
    //{ path: 'basics', loadChildren: () => import('./feature-basics/basics.module').then(m => m.BasicsModule) },
    //{ path: 'extras', loadChildren: () => import('./feature-extras/extras.module').then(m => m.ExtrasModule) },
    //{ path: 'should-login', component: ShouldLoginComponent },
    //{ path: '**', component: FallbackComponent },], {})
  ],
  providers: [
    MessageService,
    ConfirmationService,
    DialogService,
    DynamicDialogRef,
    DynamicDialogConfig,
    { provide: LOCALE_ID, useValue: 'en' }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
