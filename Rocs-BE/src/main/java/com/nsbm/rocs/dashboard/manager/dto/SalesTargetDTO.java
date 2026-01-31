package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for sales target progress
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesTargetDTO {
    private BigDecimal targetToday;
    private BigDecimal achievedToday;
    private Double progressPercentage;
}

