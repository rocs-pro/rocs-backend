package com.nsbm.rocs.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosProductDTO {
    private Long productId;
    private String name;
    private BigDecimal sellingPrice;
    private String sku;
    private String barcode;
    private BigDecimal taxRate;
    private String categoryName;
    private Boolean isSerialized;
    private BigDecimal availableStock;
}

