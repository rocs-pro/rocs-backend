package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSerialDTO {

    private Long serialId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotBlank(message = "Serial number is required")
    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNo;

    @Size(max = 60, message = "Barcode must not exceed 60 characters")
    private String barcode;

    private Long batchId;

    @Pattern(regexp = "IN_STOCK|SOLD|DAMAGED|RETURNED", message = "Invalid status")
    private String status = "IN_STOCK";

    private Long grnId;
    private Long saleId;
    private LocalDateTime createdAt;
    private LocalDateTime soldAt;

    // Display fields
    private String productName;
    private String productSku;
    private String branchName;
    private String batchCode;
}

