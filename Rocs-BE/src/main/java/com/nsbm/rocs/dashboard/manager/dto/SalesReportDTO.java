package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for sales reports
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportDTO {
    private String date;
    private Integer invoices;
    private BigDecimal revenue;
    private BigDecimal profit;
}

