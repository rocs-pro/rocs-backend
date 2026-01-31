package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for top selling products
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingProductDTO {
    private String name;
    private Integer units;
    private String revenue;
}

