package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GRNStatsDTO {

    private Long totalGRNs;
    private Long pendingGRNs;
    private Long approvedGRNs;
    private BigDecimal totalValue;
    private BigDecimal unpaidAmount;
    private BigDecimal paidAmount;
    private Long totalItems;
    private Long uniqueProducts;
    private Long activeSuppliers;
}
