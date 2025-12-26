import { Component, Input, Output, EventEmitter } from '@angular/core';

/**
 * Confirmation Dialog Component
 * Reusable confirmation modal
 */
@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {
  @Input() title: string = 'Confirm Action';
  @Input() message: string = 'Are you sure you want to proceed?';
  @Input() confirmText: string = 'Confirm';
  @Input() cancelText: string = 'Cancel';
  @Input() confirmButtonClass: string = 'btn-primary';
  @Input() show: boolean = false;

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
  @Output() close = new EventEmitter<void>();

  /**
   * Handle confirm button click
   */
  onConfirm(): void {
    this.confirm.emit();
    this.hide();
  }

  /**
   * Handle cancel button click
   */
  onCancel(): void {
    this.cancel.emit();
    this.hide();
  }

  /**
   * Handle close button or overlay click
   */
  onClose(): void {
    this.close.emit();
    this.hide();
  }

  /**
   * Hide dialog
   */
  hide(): void {
    this.show = false;
  }

  /**
   * Show dialog
   */
  showDialog(): void {
    this.show = true;
  }
}

