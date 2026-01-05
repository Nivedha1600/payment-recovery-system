import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Draft Invoices Component
 * Placeholder for draft invoice management
 */
@Component({
  selector: 'app-draft-invoices',
  templateUrl: './draft-invoices.component.html',
  styleUrls: ['./draft-invoices.component.scss']
})
export class DraftInvoicesComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  /**
   * Go back to dashboard
   */
  goBack(): void {
    this.router.navigate(['/company/dashboard']);
  }

}

