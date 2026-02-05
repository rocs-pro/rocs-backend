package com.nsbm.rocs.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDTO {
    private Long id;
    private String category;
    private String reference;
    private String requestedBy;
    private String username;
    private String email;
    private String time;
    private String approvedAt;
    private String status;
    private String description;
}
