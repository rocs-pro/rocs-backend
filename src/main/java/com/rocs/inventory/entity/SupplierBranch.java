package com.rocs.inventory.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_branches")
public class SupplierBranch {

    @EmbeddedId
    private SupplierBranchId id;

    @MapsId("supplierId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    // branchId is part of EmbeddedId; if you have Branch entity, you can map it separately.
    @Column(name = "is_preferred")
    private Boolean isPreferred = false;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public SupplierBranch() {}

    public SupplierBranch(SupplierBranchId id) { this.id = id; }

    public SupplierBranchId getId() { return id; }
    public void setId(SupplierBranchId id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; if (this.id == null) this.id = new SupplierBranchId(); this.id.setSupplierId(supplier != null ? supplier.getSupplierId() : null); }

    public Long getBranchId() { return id != null ? id.getBranchId() : null; }
    public void setBranchId(Long branchId) { if (this.id == null) this.id = new SupplierBranchId(); this.id.setBranchId(branchId); }

    public Boolean getIsPreferred() { return isPreferred; }
    public void setIsPreferred(Boolean isPreferred) { this.isPreferred = isPreferred; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

