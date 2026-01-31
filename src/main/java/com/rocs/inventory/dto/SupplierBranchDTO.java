package com.rocs.inventory.dto;

import java.math.BigDecimal;

public class SupplierBranchDTO {
    private Long branchId;
    private Boolean isPreferred;
    private BigDecimal discountPercentage;
    private String notes;

    // getters and setters
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Boolean getIsPreferred() { return isPreferred; }
    public void setIsPreferred(Boolean isPreferred) { this.isPreferred = isPreferred; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

