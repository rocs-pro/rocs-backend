package com.nsbm.rocs.pos.dto.sale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class SaleItemResponse {

    private Long saleItemId;
    private Long productId;
    private String productName;
    private String sku;
    private String barcode;
    private Long serialId;
    private String serialNo;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal total;
}