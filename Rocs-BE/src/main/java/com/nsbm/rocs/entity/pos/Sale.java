package com.nsbm.rocs.entity.pos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class Sale {

    private Long saleId;
    private String invoiceNo;
    private Long branchId;
    private Long cashierId;
    private Long customerId;
    private Long shiftId;
    private LocalDateTime saleDate;

    // Money calculations
    private BigDecimal grossTotal;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal netTotal;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;

    // Status
    private String paymentStatus; // PAID, PARTIAL, PENDING
    private String saleType; // RETAIL, WHOLESALE

    private String notes;
    private LocalDateTime createdAt;


    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", netTotal=" + netTotal +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}