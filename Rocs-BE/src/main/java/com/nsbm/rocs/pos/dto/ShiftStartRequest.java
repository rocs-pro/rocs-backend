package com.nsbm.rocs.pos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("cashierId")
    private Long cashierId;

    @NotNull(message = "Branch ID is required")
    @JsonProperty("branchId")
    private Long branchId;

    @NotNull(message = "Terminal ID is required")
    @JsonProperty("terminalId")
    private Long terminalId;

    @NotNull(message = "Opening cash is required")
    @PositiveOrZero(message = "Opening cash cannot be negative")
    @JsonProperty("openingCash")
    private BigDecimal openingCash;

    @Valid
    @JsonProperty("supervisor")
    private SupervisorAuth supervisor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupervisorAuth {
        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;
    }
}
