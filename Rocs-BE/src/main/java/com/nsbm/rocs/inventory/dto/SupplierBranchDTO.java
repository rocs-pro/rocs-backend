package com.nsbm.rocs.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SupplierBranchDTO {

    @NotNull(message = "Branch id is required")
    private Long branchId;

    private Boolean isPreferred = false;

    private BigDecimal discountPercentage;

    private String notes;
}

