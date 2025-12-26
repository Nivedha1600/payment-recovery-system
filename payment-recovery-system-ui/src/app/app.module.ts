import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { AdminLayoutComponent } from './layout/admin-layout/admin-layout.component';
import { CompanyLayoutComponent } from './layout/company-layout/company-layout.component';
import { AuthLayoutComponent } from './layout/auth-layout/auth-layout.component';

/**
 * Root application module
 */
@NgModule({
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    CompanyLayoutComponent,
    AuthLayoutComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    CoreModule,  // Core module imported once here
    SharedModule, // Shared module for common components
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

