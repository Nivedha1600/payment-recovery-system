import { Component, Input } from '@angular/core';
import { Location } from '@angular/common';

/**
 * Back Button Component
 * Reusable component for navigating back
 */
@Component({
  selector: 'app-back-button',
  template: `
    <button class="btn btn-secondary btn-back" (click)="goBack()" [title]="tooltip">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M19 12H5"></path>
        <polyline points="12 19 5 12 12 5"></polyline>
      </svg>
      <span>{{ label }}</span>
    </button>
  `,
  styles: [`
    .btn-back {
      display: inline-flex;
      align-items: center;
      gap: 8px;
    }
  `]
})
export class BackButtonComponent {
  @Input() label: string = 'Back';
  @Input() tooltip: string = 'Go back';
  @Input() route?: string; // Optional specific route to navigate to

  constructor(private location: Location) {}

  goBack(): void {
    if (this.route) {
      // Navigate to specific route if provided
      window.location.href = this.route;
    } else {
      // Use browser back
      this.location.back();
    }
  }
}

