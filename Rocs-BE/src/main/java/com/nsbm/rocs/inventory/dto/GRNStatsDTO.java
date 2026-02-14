package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GRNStatsDTO {

    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalGRNs;
    private Long pendingGRNs;
    private Long approvedGRNs;
    private Long rejectedGRNs;
    private BigDecimal totalValue;
    private BigDecimal unpaidAmount;
    private BigDecimal paidAmount;
    private Long totalItems;
    private Long uniqueProducts;
    private Long activeSuppliers;
}
