package com.nsbm.rocs.pos.dto.sale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * PURPOSE: Represents one payment method used in sale
 * EXAMPLE: $500 by CASH + $500 by CARD
 */
public class PaymentRequest {

    @NotBlank(message = "Payment type is required")
    private String paymentType; // CASH, CARD, QR, BANK_TRANSFER

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String referenceNo; // For card/bank payments (optional)
    private String cardLast4; // Last 4 digits of card (optional)

    public PaymentRequest() {}

    // Getters and Setters
    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "paymentType='" + paymentType + '\'' +
                ", amount=" + amount +
                ", referenceNo='" + referenceNo + '\'' +
                '}';
    }
}