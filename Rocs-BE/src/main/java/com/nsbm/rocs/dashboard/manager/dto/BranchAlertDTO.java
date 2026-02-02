package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for branch alerts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchAlertDTO {
    private String type; // Info, Warning, Critical
    private String message;
    private String time;
}

