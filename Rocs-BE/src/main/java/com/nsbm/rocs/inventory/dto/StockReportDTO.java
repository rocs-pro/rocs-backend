package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReportDTO {

    private Long productId;
    private String sku;
    private String productName;
    private String categoryName;
    private String brandName;
    private Integer totalStock;
    private Integer reservedQty;
    private Integer availableQty;
    private BigDecimal costValue;
    private BigDecimal sellingValue;
    private Integer reorderLevel;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK, OVERSTOCKED
}

