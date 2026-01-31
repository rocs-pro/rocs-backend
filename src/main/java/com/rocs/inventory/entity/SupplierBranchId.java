package com.rocs.inventory.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SupplierBranchId implements Serializable {
    private Long supplierId;
    private Long branchId;

    public SupplierBranchId() {}

    public SupplierBranchId(Long supplierId, Long branchId) {
        this.supplierId = supplierId;
        this.branchId = branchId;
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierBranchId)) return false;
        SupplierBranchId that = (SupplierBranchId) o;
        return Objects.equals(supplierId, that.supplierId) &&
               Objects.equals(branchId, that.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, branchId);
    }
}

