package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertDTO {

    private Long productId;
    private String sku;
    private String productName;
    private String categoryName;
    private String brandName;
    private Long branchId;
    private String branchName;
    private Integer currentStock;
    private Integer reorderLevel;
    private Integer shortfall;
    private String alertLevel; // CRITICAL, WARNING
    private String message;
}
