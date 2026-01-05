package com.paymentrecovery.config;

import com.paymentrecovery.model.entity.Company;
import com.paymentrecovery.model.entity.User;
import com.paymentrecovery.model.enums.UserRole;
import com.paymentrecovery.repository.CompanyRepository;
import com.paymentrecovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer for creating default admin user and company
 * Runs on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.company.name:Payment Recovery System Admin}")
    private String adminCompanyName;

    @Value("${app.admin.company.gst:ADMIN-GST-001}")
    private String adminCompanyGst;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Create or get admin company
        Company adminCompany = createOrGetAdminCompany();
        
        // Create admin user if it doesn't exist
        createAdminUserIfNotExists(adminCompany);
        
        log.info("Data initialization completed.");
    }

    /**
     * Create or get the admin company
     */
    private Company createOrGetAdminCompany() {
        return companyRepository.findByGstNumber(adminCompanyGst)
            .orElseGet(() -> {
                log.info("Creating admin company: {}", adminCompanyName);
                Company company = new Company();
                company.setName(adminCompanyName);
                company.setGstNumber(adminCompanyGst);
                company.setIsActive(true);
                company.setIsApproved(true); // Admin company is auto-approved
                Company saved = companyRepository.save(company);
                log.info("Admin company created with ID: {}", saved.getId());
                return saved;
            });
    }

    /**
     * Create admin user if it doesn't exist
     */
    private void createAdminUserIfNotExists(Company company) {
        if (!userRepository.existsByUsernameAndCompanyId(adminUsername, company.getId())) {
            log.info("Creating admin user: {}", adminUsername);
            
            User adminUser = new User();
            adminUser.setCompany(company);
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setIsActive(true);
            
            User saved = userRepository.save(adminUser);
            log.info("Admin user created with ID: {} and username: {}", saved.getId(), saved.getUsername());
            log.warn("Default admin credentials - Username: {}, Password: {} - PLEASE CHANGE IN PRODUCTION!", 
                    adminUsername, adminPassword);
        } else {
            log.info("Admin user already exists, skipping creation.");
        }
    }
}

