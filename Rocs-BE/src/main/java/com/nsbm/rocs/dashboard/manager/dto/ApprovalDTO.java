package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for approval items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDTO {
    private String id;
    private String category;
    private String reference;
    private String requestedBy;
    private String time;
    private String status;
}

