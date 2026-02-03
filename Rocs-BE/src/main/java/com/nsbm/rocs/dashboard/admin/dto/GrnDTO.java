package com.nsbm.rocs.dashboard.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing GRN data for admin APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrnDTO {
    // primary identifier
    private Long id;

    // core fields
    private String grnNo;
    private Long branchId;
    private Long supplierId;
    private Long poId;
    private LocalDate grnDate;
    private String invoiceNo;
    private LocalDate invoiceDate;

    // amounts
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;

    // statuses / notes
    private String paymentStatus;
    private String status;
    private String notes;

    // audit
    private Long createdBy;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // presentation / joined fields used by admin endpoints
    private String supplierName;
    private Integer itemCount;
}
