package com.paymentrecovery.service;

import com.paymentrecovery.model.dto.request.CompanyRegistrationRequest;
import com.paymentrecovery.model.dto.response.CompanyRegistrationResponse;
import com.paymentrecovery.model.entity.Company;
import com.paymentrecovery.model.entity.User;
import com.paymentrecovery.model.enums.UserRole;
import com.paymentrecovery.repository.CompanyRepository;
import com.paymentrecovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Company registration service
 * Handles company registration with admin approval workflow
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyRegistrationService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new company
     * Company will be created in PENDING approval status
     * Admin must approve before users can login
     * 
     * @param request Registration request
     * @return Registration response
     */
    @Transactional
    public CompanyRegistrationResponse registerCompany(CompanyRegistrationRequest request) {
        log.info("Processing company registration for: {}", request.getCompanyName());

        // Check if GST number already exists (if provided)
        if (request.getGstNumber() != null && !request.getGstNumber().trim().isEmpty()) {
            companyRepository.findByGstNumber(request.getGstNumber())
                .ifPresent(company -> {
                    throw new RuntimeException("Company with GST number " + request.getGstNumber() + " already exists");
                });
        }

        // Check if username already exists globally (for company users)
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists. Please choose a different username.");
        }

        // Create company with pending approval
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setGstNumber(request.getGstNumber());
        company.setContactEmail(request.getContactEmail());
        company.setContactPhone(request.getContactPhone());
        company.setIsActive(true); // Active but not approved
        company.setIsApproved(false); // Requires admin approval
        
        Company savedCompany = companyRepository.save(company);
        log.info("Company created with ID: {} - Status: PENDING APPROVAL", savedCompany.getId());

        // Create admin user for the company
        User adminUser = new User();
        adminUser.setCompany(savedCompany);
        adminUser.setUsername(request.getUsername());
        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUser.setRole(UserRole.ACCOUNT); // Company admin role
        adminUser.setIsActive(true);
        
        User savedUser = userRepository.save(adminUser);
        log.info("Company admin user created with ID: {} and username: {}", savedUser.getId(), savedUser.getUsername());

        // Build response
        CompanyRegistrationResponse response = new CompanyRegistrationResponse();
        response.setCompanyId(savedCompany.getId());
        response.setCompanyName(savedCompany.getName());
        response.setRequiresApproval(true);
        response.setMessage("Company registration successful. Your account is pending admin approval. " +
                "You will be able to login once an administrator approves your registration.");

        return response;
    }
}

