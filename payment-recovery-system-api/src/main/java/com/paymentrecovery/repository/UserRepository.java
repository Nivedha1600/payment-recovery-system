package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.User;
import com.paymentrecovery.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username and company ID
     */
    Optional<User> findByUsernameAndCompanyId(String username, Long companyId);
    
    /**
     * Find user by username (across all companies)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find all users by company ID
     */
    List<User> findByCompanyId(Long companyId);
    
    /**
     * Find all users by role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Check if user exists by username and company ID
     */
    boolean existsByUsernameAndCompanyId(String username, Long companyId);
}

