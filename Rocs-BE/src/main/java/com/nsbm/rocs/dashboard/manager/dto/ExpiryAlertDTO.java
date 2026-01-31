package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for expiry alerts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpiryAlertDTO {
    private String item;
    private String expiresOn;
    private Integer qty;
    private String severity; // Critical, Warning
}

