package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for pending GRNs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingGrnDTO {
    private String id;
    private String supplier;
    private Integer items;
    private String eta;
}

