package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {

    private Long stockId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantity = 0;

    @Min(value = 0, message = "Reserved quantity must be positive")
    private Integer reservedQty = 0;

    private Integer availableQty;

    private LocalDateTime lastUpdated;

    // Display fields
    private String productName;
    private String productSku;
    private String branchName;
}

