package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Company entity
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    Optional<Company> findByGstNumber(String gstNumber);
    
    /**
     * Find companies by approval status
     */
    List<Company> findByIsApproved(Boolean isApproved);
}

