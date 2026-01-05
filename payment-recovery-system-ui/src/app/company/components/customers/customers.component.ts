import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Customers Component
 * Placeholder for customer management
 */
@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.scss']
})
export class CustomersComponent implements OnInit {

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

