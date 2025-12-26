package com.paymentrecovery.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Customer entity - Represents a customer of a company
 * Each customer belongs to a company (multi-tenant)
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_company_id", columnList = "company_id"),
    @Index(name = "idx_customer_email", columnList = "email"),
    @Index(name = "idx_customer_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer_company"))
    @NotNull(message = "Company is required")
    private Company company;

    @Column(name = "customer_name", nullable = false, length = 255)
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 255)
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "payment_terms_days", nullable = false)
    @NotNull(message = "Payment terms days is required")
    private Integer paymentTermsDays = 30;
}

