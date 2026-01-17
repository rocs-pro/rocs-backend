package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {

    private Long batchId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotBlank(message = "Batch code is required")
    @Size(max = 60, message = "Batch code must not exceed 60 characters")
    private String batchCode;

    private LocalDate manufacturingDate;

    private LocalDate expiryDate;

    @Min(value = 0, message = "Quantity must be positive")
    private Integer qty = 0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be positive")
    private BigDecimal costPrice;

    // Display fields
    private String productName;
    private String productSku;
    private String branchName;
    private Long daysToExpiry;
    private String expiryStatus; // FRESH, EXPIRING_SOON, EXPIRED
}

