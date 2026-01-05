package com.paymentrecovery.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating company profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyProfileRequest {
    
    @NotBlank(message = "Company name is required")
    private String name;
    
    private String gstNumber;
    
    @Email(message = "Contact email should be valid")
    private String contactEmail;
    
    private String contactPhone;
}

