package com.paymentrecovery.model.entity;

import com.paymentrecovery.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User entity - Represents a user in the system
 * Each user belongs to a company (multi-tenant)
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_company_id", columnList = "company_id"),
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_is_active", columnList = "is_active")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_username_company", columnNames = {"username", "company_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_company"))
    @NotNull(message = "Company is required")
    private Company company;

    @Column(name = "username", nullable = false, length = 100)
    @NotBlank(message = "Username is required")
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @NotNull(message = "Role is required")
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    @NotNull(message = "IsActive is required")
    private Boolean isActive = true;
}

