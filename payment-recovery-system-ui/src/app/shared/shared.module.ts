import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { TopbarComponent } from './components/topbar/topbar.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';

/**
 * Shared module
 * Contains reusable components, directives, and pipes
 * Imported by feature modules as needed
 */
@NgModule({
  declarations: [
    SidebarComponent,
    TopbarComponent,
    LoadingSpinnerComponent,
    ConfirmDialogComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    CommonModule,
    RouterModule,
    SidebarComponent,
    TopbarComponent,
    LoadingSpinnerComponent,
    ConfirmDialogComponent
  ]
})
export class SharedModule { }

