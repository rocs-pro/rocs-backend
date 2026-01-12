package com.nsbm.rocs.pos.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * PURPOSE: Complete sale transaction data from frontend
 * EXAMPLE: Customer buys 3 items, pays with 2 methods
 */
public class CreateSaleRequest {

    private Long customerId; // Optional - can be walk-in customer

    @NotEmpty(message = "Sale items are required")
    @Valid
    private List<SaleItemRequest> items;

    @NotEmpty(message = "Payments are required")
    @Valid
    private List<PaymentRequest> payments;

    private BigDecimal discount; // Total sale discount (optional)

    private String notes; // Sale notes (optional)

    public CreateSaleRequest() {}

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<SaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }

    public List<PaymentRequest> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentRequest> payments) {
        this.payments = payments;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CreateSaleRequest{" +
                "customerId=" + customerId +
                ", items=" + items +
                ", payments=" + payments +
                ", discount=" + discount +
                ", notes='" + notes + '\'' +
                '}';
    }
}