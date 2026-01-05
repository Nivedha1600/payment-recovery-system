import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest, CompanyRegistrationRequest } from '../../../core/models/auth.model';

/**
 * Login component
 * Handles user authentication and company registration
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  registrationForm!: FormGroup;
  isRegistrationMode = false;
  isLoading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  returnUrl: string | null = null;
  sessionExpired = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // Initialize login form
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Initialize registration form
    this.registrationForm = this.formBuilder.group({
      companyName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      gstNumber: ['', [Validators.maxLength(50)]],
      contactEmail: ['', [Validators.required, Validators.email]],
      contactPhone: ['', [Validators.maxLength(20)]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]]
    });
  }

  ngOnInit(): void {
    // Get return URL from route parameters or default to dashboard
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || null;
    
    // Check if session expired
    this.sessionExpired = this.route.snapshot.queryParams['sessionExpired'] === 'true';

    // If already authenticated, redirect to appropriate dashboard
    if (this.authService.isAuthenticated()) {
      this.redirectToDashboard();
    }
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.markFormGroupTouched(this.loginForm);
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const credentials: LoginRequest = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password
    };

    this.authService.login(credentials).subscribe({
      next: () => {
        this.isLoading = false;
        this.redirectToDashboard();
      },
      error: (error) => {
        this.isLoading = false;
        this.handleLoginError(error);
      }
    });
  }

  /**
   * Redirect to appropriate dashboard based on user role
   */
  private redirectToDashboard(): void {
    const user = this.authService.getCurrentUser();
    
    if (!user) {
      this.router.navigate(['/auth/login']);
      return;
    }

    // Redirect to return URL if exists, otherwise to role-based dashboard
    if (this.returnUrl) {
      this.router.navigateByUrl(this.returnUrl);
    } else if (user.role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (user.role === 'COMPANY') {
      this.router.navigate(['/company/dashboard']);
    } else {
      this.router.navigate(['/auth/login']);
    }
  }

  /**
   * Handle login errors
   */
  private handleLoginError(error: any): void {
    if (error.status === 401) {
      this.errorMessage = 'Invalid username or password';
    } else if (error.status === 0) {
      this.errorMessage = 'Unable to connect to server. Please try again later.';
    } else {
      this.errorMessage = error.error?.message || 'An error occurred during login. Please try again.';
    }
  }

  /**
   * Mark all form fields as touched to show validation errors
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Get form control for template access
   */
  get f() {
    return this.loginForm.controls;
  }

  /**
   * Get registration form control for template access
   */
  get rf() {
    return this.registrationForm.controls;
  }

  /**
   * Toggle between login and registration mode
   */
  toggleMode(): void {
    this.isRegistrationMode = !this.isRegistrationMode;
    this.errorMessage = null;
    this.successMessage = null;
    
    // Reset forms
    this.loginForm.reset();
    this.registrationForm.reset();
  }

  /**
   * Handle registration form submission
   */
  onRegister(): void {
    if (this.registrationForm.invalid) {
      this.markFormGroupTouched(this.registrationForm);
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const registrationData: CompanyRegistrationRequest = {
      companyName: this.registrationForm.value.companyName,
      gstNumber: this.registrationForm.value.gstNumber || undefined,
      contactEmail: this.registrationForm.value.contactEmail,
      contactPhone: this.registrationForm.value.contactPhone || undefined,
      username: this.registrationForm.value.username,
      password: this.registrationForm.value.password
    };

    this.authService.registerCompany(registrationData).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = response.message;
        // Reset form after successful registration
        this.registrationForm.reset();
        // Optionally switch back to login mode after 3 seconds
        setTimeout(() => {
          this.isRegistrationMode = false;
          this.successMessage = null;
        }, 5000);
      },
      error: (error) => {
        this.isLoading = false;
        this.handleRegistrationError(error);
      }
    });
  }

  /**
   * Handle registration errors
   */
  private handleRegistrationError(error: any): void {
    if (error.status === 400) {
      this.errorMessage = error.error?.message || 'Registration failed. Please check your information and try again.';
    } else if (error.status === 0) {
      this.errorMessage = 'Unable to connect to server. Please try again later.';
    } else {
      this.errorMessage = error.error?.message || 'An error occurred during registration. Please try again.';
    }
  }
}

