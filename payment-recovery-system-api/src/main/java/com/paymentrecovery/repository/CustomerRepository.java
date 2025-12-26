package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByCompanyId(Long companyId);
}

