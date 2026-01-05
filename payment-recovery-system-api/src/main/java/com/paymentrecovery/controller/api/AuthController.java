package com.paymentrecovery.controller.api;

import com.paymentrecovery.model.dto.request.CompanyRegistrationRequest;
import com.paymentrecovery.model.dto.request.LoginRequest;
import com.paymentrecovery.model.dto.response.CompanyRegistrationResponse;
import com.paymentrecovery.model.dto.response.LoginResponse;
import com.paymentrecovery.service.AuthService;
import com.paymentrecovery.service.CompanyRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller
 * Handles user authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final CompanyRegistrationService registrationService;

    /**
     * User login endpoint
     * 
     * @param loginRequest Login credentials
     * @return LoginResponse with token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getUsername());
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Company registration endpoint
     * Public endpoint - no authentication required
     * 
     * @param registrationRequest Company registration details
     * @return Registration response
     */
    @PostMapping("/register")
    public ResponseEntity<CompanyRegistrationResponse> register(@Valid @RequestBody CompanyRegistrationRequest registrationRequest) {
        try {
            log.info("Company registration request for: {}", registrationRequest.getCompanyName());
            CompanyRegistrationResponse response = registrationService.registerCompany(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Health check endpoint for auth service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}

