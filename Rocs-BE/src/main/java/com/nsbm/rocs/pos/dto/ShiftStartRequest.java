package com.nsbm.rocs.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftStartRequest {

    @NotNull(message = "Cashier ID is required")
    private Long cashierId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotNull(message = "Terminal ID is required")
    private Long terminalId;

    @NotNull(message = "Opening cash is required")
    @PositiveOrZero(message = "Opening cash cannot be negative")
    private BigDecimal openingCash;

    @Valid
    private SupervisorAuth supervisor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupervisorAuth {
        private String username;
        private String password;
    }
}
