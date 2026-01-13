package com.nsbm.rocs.entity.pos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class SaleItem {

    private Long saleItemId;
    private Long saleId;
    private Long productId;
    private Long serialId; // For IMEI items
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal taxRate;
    private BigDecimal total;


    @Override
    public String toString() {
        return "SaleItem{" +
                "saleItemId=" + saleItemId +
                ", productId=" + productId +
                ", qty=" + qty +
                ", total=" + total +
                '}';
    }
}