package com.nsbm.rocs.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportDTO {
    private String date;
    private Integer invoices;
    private BigDecimal revenue;
    private BigDecimal profit;
}

