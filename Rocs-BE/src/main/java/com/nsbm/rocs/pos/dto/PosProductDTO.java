package com.nsbm.rocs.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String sku;
    private String barcode;
}

