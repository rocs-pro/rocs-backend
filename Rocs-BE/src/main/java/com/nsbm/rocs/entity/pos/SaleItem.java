package com.nsbm.rocs.entity.pos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Setter
@Getter
@NoArgsConstructor
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_item_id")
    private Long saleItemId;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "serial_id")
    private Long serialId; // For IMEI items

    private Integer qty;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private BigDecimal discount;

    @Column(name = "tax_rate")
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