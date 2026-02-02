package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for staff summary
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffSummaryDTO {
    private String name;
    private String role;
    private String lastLogin;
    private String status; // Active, Offline
}

