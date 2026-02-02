package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;

    @NotBlank(message = "SKU is required")
    @Size(max = 60, message = "SKU must not exceed 60 characters")
    private String sku;

    @Size(max = 60, message = "Barcode must not exceed 60 characters")
    private String barcode;

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Long categoryId;
    private Long subcategoryId;
    private Long brandId;
    private Long unitId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be positive")
    private BigDecimal costPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be positive")
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "MRP must be positive")
    private BigDecimal mrp = BigDecimal.ZERO;

    @Min(value = 0, message = "Reorder level must be positive")
    private Integer reorderLevel = 0;

    @Min(value = 0, message = "Max stock level must be positive")
    private Integer maxStockLevel = 0;

    private Boolean isSerialized = false;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be positive")
    @DecimalMax(value = "100.0", inclusive = true, message = "Tax rate must not exceed 100%")
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Min(value = 0, message = "Warranty months must be positive")
    private Integer warrantyMonths = 0;

    private Boolean isActive = true;

    // Display fields
    private String categoryName;
    private String subcategoryName;
    private String brandName;
    private String unitName;
    private String unitSymbol;
}

