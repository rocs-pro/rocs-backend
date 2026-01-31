package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentDTO {

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Adjustment quantity is required")
    private Integer adjustmentQty; // Can be positive or negative

    @NotBlank(message = "Adjustment type is required")
    @Pattern(regexp = "ADD|REMOVE|DAMAGE|LOSS|RETURN|CORRECTION", message = "Invalid adjustment type")
    private String adjustmentType;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    private String batchCode;
}

