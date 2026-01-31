package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for stock alerts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlertDTO {
    private String item;
    private Integer qty;
    private String level; // Critical, Warning
}

