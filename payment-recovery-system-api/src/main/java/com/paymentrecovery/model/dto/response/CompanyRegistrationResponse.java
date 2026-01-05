package com.paymentrecovery.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Company registration response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRegistrationResponse {
    
    private Long companyId;
    private String companyName;
    private String message;
    private Boolean requiresApproval;
}

