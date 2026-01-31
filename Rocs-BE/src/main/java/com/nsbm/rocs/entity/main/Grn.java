package com.nsbm.rocs.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a Goods Received Note (GRN)
 * Maps to the 'grns' table in the database
 */
@Entity
@Table(name = "grns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_id")
    private Long grnId;

    @Column(name = "grn_no", unique = true, nullable = false, length = 50)
    private String grnNo;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "po_id")
    private Long poId;

    @Column(name = "grn_date", nullable = false)
    private java.time.LocalDate grnDate;

    @Column(name = "invoice_no", length = 100)
    private String invoiceNo;

    @Column(name = "invoice_date")
    private java.time.LocalDate invoiceDate;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 15, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Column(name = "payment_status", length = 30)
    private String paymentStatus = "UNPAID"; // UNPAID, PARTIALLY_PAID, PAID

    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

