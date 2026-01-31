package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Profit and Loss report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfitLossDTO {
    private String period;
    private BigDecimal revenue;
    private BigDecimal cogs;
    private BigDecimal grossProfit;
    private List<ExpenseDTO> expenses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpenseDTO {
        private String name;
        private BigDecimal amount;
    }
}

