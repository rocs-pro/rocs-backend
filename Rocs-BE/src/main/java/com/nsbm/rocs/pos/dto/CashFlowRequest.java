package com.nsbm.rocs.pos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CashFlowRequest {

    @NotNull(message = "Type is required")
    private String type; // PAID_IN, PAID_OUT

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String reason;

    private String referenceNo;
}
