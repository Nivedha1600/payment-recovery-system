package com.paymentrecovery.controller.api;

import com.paymentrecovery.model.entity.Company;
import com.paymentrecovery.repository.CompanyRepository;
import com.paymentrecovery.repository.InvoiceRepository;
import com.paymentrecovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin controller
 * Handles admin-only endpoints for platform management
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Get platform metrics for admin dashboard
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getPlatformMetrics() {
        log.info("Fetching platform metrics for admin dashboard");
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Company metrics
        long totalCompanies = companyRepository.count();
        long activeCompanies = companyRepository.count();
        long inactiveCompanies = 0;
        
        // User metrics
        long totalUsers = userRepository.count();
        
        // Invoice metrics (placeholder - implement when Invoice entity is ready)
        long totalInvoices = invoiceRepository.count();
        long pendingInvoices = 0;
        long paidInvoices = 0;
        
        // Revenue (placeholder)
        double totalRevenue = 0.0;
        
        metrics.put("totalCompanies", totalCompanies);
        metrics.put("activeCompanies", activeCompanies);
        metrics.put("inactiveCompanies", inactiveCompanies);
        metrics.put("totalUsers", totalUsers);
        metrics.put("totalInvoices", totalInvoices);
        metrics.put("pendingInvoices", pendingInvoices);
        metrics.put("paidInvoices", paidInvoices);
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("recentActivity", new Object[0]); // Placeholder
        
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get list of companies with pagination
     */
    @GetMapping("/companies")
    public ResponseEntity<Map<String, Object>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        log.info("Fetching companies list - page: {}, size: {}, search: {}", page, size, search);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companiesPage;
        
        if (search != null && !search.trim().isEmpty()) {
            // Simple search - can be enhanced with proper search logic
            companiesPage = companyRepository.findAll(pageable);
        } else {
            companiesPage = companyRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("companies", companiesPage.getContent());
        response.put("totalElements", companiesPage.getTotalElements());
        response.put("totalPages", companiesPage.getTotalPages());
        response.put("currentPage", page);
        response.put("pageSize", size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get company by ID
     */
    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        log.info("Fetching company by ID: {}", id);
        
        return companyRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update company status (activate/deactivate)
     */
    @PatchMapping("/companies/{id}/status")
    public ResponseEntity<Company> updateCompanyStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> statusUpdate) {
        
        log.info("Updating company status - ID: {}, isActive: {}", id, statusUpdate.get("isActive"));
        
        return companyRepository.findById(id)
            .map(company -> {
                company.setIsActive(statusUpdate.get("isActive"));
                Company updated = companyRepository.save(company);
                log.info("Company status updated - ID: {}, isActive: {}", id, updated.getIsActive());
                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Approve company registration
     * Allows company users to login after approval
     */
    @PostMapping("/companies/{id}/approve")
    public ResponseEntity<Company> approveCompany(@PathVariable Long id) {
        log.info("Approving company - ID: {}", id);
        
        return companyRepository.findById(id)
            .map(company -> {
                company.setIsApproved(true);
                company.setIsActive(true);
                Company updated = companyRepository.save(company);
                log.info("Company approved - ID: {}, Name: {}", id, updated.getName());
                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Reject company registration
     */
    @PostMapping("/companies/{id}/reject")
    public ResponseEntity<Company> rejectCompany(@PathVariable Long id) {
        log.info("Rejecting company - ID: {}", id);
        
        return companyRepository.findById(id)
            .map(company -> {
                company.setIsApproved(false);
                company.setIsActive(false);
                Company updated = companyRepository.save(company);
                log.info("Company rejected - ID: {}, Name: {}", id, updated.getName());
                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get pending companies (awaiting approval)
     */
    @GetMapping("/companies/pending")
    public ResponseEntity<Map<String, Object>> getPendingCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching pending companies - page: {}, size: {}", page, size);
        
        List<Company> pendingCompanies = companyRepository.findByIsApproved(false);
        
        // Manual pagination (can be improved with Pageable query)
        int start = page * size;
        int end = Math.min(start + size, pendingCompanies.size());
        List<Company> paginatedCompanies = start < pendingCompanies.size() 
            ? pendingCompanies.subList(start, end) 
            : List.of();
        
        Map<String, Object> response = new HashMap<>();
        response.put("companies", paginatedCompanies);
        response.put("totalElements", pendingCompanies.size());
        response.put("totalPages", (int) Math.ceil((double) pendingCompanies.size() / size));
        response.put("currentPage", page);
        response.put("pageSize", size);
        
        return ResponseEntity.ok(response);
    }
}

