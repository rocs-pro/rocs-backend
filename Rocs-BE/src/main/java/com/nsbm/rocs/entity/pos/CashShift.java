package com.nsbm.rocs.entity.pos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashShift {

    // Primary Key
    private Long shiftId;

    // Foreign Keys
    private Long branchId;
    private Long cashierId;

    // Timestamps
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    // Money fields
    private BigDecimal openingCash;
    private BigDecimal closingCash;
    private BigDecimal expectedCash;
    private BigDecimal cashDifference;
    private BigDecimal totalSales;
    private BigDecimal totalReturns;

    // Status and notes
    private String status; // OPEN, CLOSED
    private String notes;

    @Override
    public String toString() {
        return "CashShift{" +
                "shiftId=" + shiftId +
                ", branchId=" + branchId +
                ", cashierId=" + cashierId +
                ", openedAt=" + openedAt +
                ", closedAt=" + closedAt +
                ", openingCash=" + openingCash +
                ", closingCash=" + closingCash +
                ", status='" + status + '\'' +
                '}';
    }
}