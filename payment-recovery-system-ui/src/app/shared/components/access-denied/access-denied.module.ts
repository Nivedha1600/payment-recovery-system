import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccessDeniedComponent } from './access-denied.component';

/**
 * Access Denied module
 * Standalone module for access denied page
 */
@NgModule({
  declarations: [AccessDeniedComponent],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class AccessDeniedModule { }

