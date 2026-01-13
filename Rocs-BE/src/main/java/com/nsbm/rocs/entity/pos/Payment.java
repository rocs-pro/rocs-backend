package com.nsbm.rocs.entity.pos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
public class Payment {

    private Long paymentId;
    private Long saleId;
    private String paymentType; // CASH, CARD, QR, BANK_TRANSFER
    private BigDecimal amount;
    private String referenceNo;
    private String cardLast4;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", paymentType='" + paymentType + '\'' +
                ", amount=" + amount +
                '}';
    }
}