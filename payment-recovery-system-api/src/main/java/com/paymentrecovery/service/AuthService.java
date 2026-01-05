package com.paymentrecovery.service;

import com.paymentrecovery.model.dto.request.LoginRequest;
import com.paymentrecovery.model.dto.response.LoginResponse;
import com.paymentrecovery.model.entity.User;
import com.paymentrecovery.model.enums.UserRole;
import com.paymentrecovery.repository.UserRepository;
import com.paymentrecovery.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service
 * Handles user authentication and token generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticate user and generate token
     * 
     * @param loginRequest Login credentials
     * @return LoginResponse with token and user info
     * @throws RuntimeException if authentication fails
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.debug("Attempting to authenticate user: {}", loginRequest.getUsername());
        
        // Find user by username (across all companies for admin, or specific company for regular users)
        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> {
                log.warn("Authentication failed: User not found - {}", loginRequest.getUsername());
                return new RuntimeException("Invalid username or password");
            });

        // Check if user is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Authentication failed: User is inactive - {}", loginRequest.getUsername());
            throw new RuntimeException("User account is inactive");
        }

        // Check if company is approved (for non-admin users)
        if (user.getRole() != UserRole.ADMIN && !Boolean.TRUE.equals(user.getCompany().getIsApproved())) {
            log.warn("Authentication failed: Company not approved - Company ID: {}, User: {}", 
                    user.getCompany().getId(), loginRequest.getUsername());
            throw new RuntimeException("Your company registration is pending admin approval. " +
                    "Please wait for administrator approval before logging in.");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: Invalid password for user - {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        // Map ACCOUNT role to COMPANY for frontend compatibility
        String roleName = user.getRole() == UserRole.ACCOUNT ? "COMPANY" : user.getRole().name();
        String token = jwtTokenProvider.generateToken(
            user.getUsername(),
            roleName,
            user.getCompany().getId()
        );

        log.info("User authenticated successfully: {} with role: {}", user.getUsername(), roleName);

        // Build response
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(roleName);
        response.setUsername(user.getUsername());

        return response;
    }
}

