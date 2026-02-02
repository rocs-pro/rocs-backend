package com.nsbm.rocs.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a sale transaction
 * Maps to the 'sales' table in the database
 */
@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "invoice_no", unique = true, nullable = false, length = 50)
    private String invoiceNo;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "cashier_id", nullable = false)
    private Long cashierId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "shift_id")
    private Long shiftId;

    @CreationTimestamp
    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    @Column(name = "gross_total", precision = 12, scale = 2)
    private BigDecimal grossTotal = BigDecimal.ZERO;

    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "net_total", precision = 12, scale = 2)
    private BigDecimal netTotal = BigDecimal.ZERO;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "change_amount", precision = 10, scale = 2)
    private BigDecimal changeAmount = BigDecimal.ZERO;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "PAID"; // PAID, PARTIAL, PENDING

    @Column(name = "sale_type", length = 20)
    private String saleType = "RETAIL"; // RETAIL, WHOLESALE

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

