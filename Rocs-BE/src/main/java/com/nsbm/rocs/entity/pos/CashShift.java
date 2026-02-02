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

    private String shiftNo;

    // Foreign Keys
    private Long branchId;
    private Long terminalId;
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

    private Long approvedBy;
    private LocalDateTime approvedAt;

    // Status and notes
    private String status; // OPEN, CLOSED
    private String notes;

    @Override
    public String toString() {
        return "CashShift{" +
                "shiftId=" + shiftId +
                ", shiftNo='" + shiftNo + '\'' +
                ", branchId=" + branchId +
                ", terminalId=" + terminalId +
                ", cashierId=" + cashierId +
                ", openedAt=" + openedAt +
                ", closedAt=" + closedAt +
                ", openingCash=" + openingCash +
                ", closingCash=" + closingCash +
                ", status='" + status + '\'' +
                '}';
    }


}