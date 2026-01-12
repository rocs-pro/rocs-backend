package com.nsbm.rocs.pos.dto.sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.nsbm.rocs.pos.dto.sale.PaymentResponse;

/**
 * PURPOSE: Complete sale data sent back to frontend
 */
public class SaleResponse {

    private Long saleId;
    private String invoiceNo;
    private Long customerId;
    private String customerName;
    private Long cashierId;
    private String cashierName;
    private Long branchId;
    private String branchName;
    private BigDecimal grossTotal;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal netTotal;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;
    private String paymentStatus;
    private LocalDateTime saleDate;
    private String notes;

    // Nested data
    private List<SaleItemResponse> items;
    private List<PaymentResponse> payments;

    // Builder pattern
    public static class Builder {
        private SaleResponse response = new SaleResponse();

        public Builder saleId(Long saleId) {
            response.saleId = saleId;
            return this;
        }

        public Builder invoiceNo(String invoiceNo) {
            response.invoiceNo = invoiceNo;
            return this;
        }

        public Builder customerId(Long customerId) {
            response.customerId = customerId;
            return this;
        }

        public Builder customerName(String customerName) {
            response.customerName = customerName;
            return this;
        }

        public Builder cashierId(Long cashierId) {
            response.cashierId = cashierId;
            return this;
        }

        public Builder cashierName(String cashierName) {
            response.cashierName = cashierName;
            return this;
        }

        public Builder branchId(Long branchId) {
            response.branchId = branchId;
            return this;
        }

        public Builder branchName(String branchName) {
            response.branchName = branchName;
            return this;
        }

        public Builder grossTotal(BigDecimal grossTotal) {
            response.grossTotal = grossTotal;
            return this;
        }

        public Builder discount(BigDecimal discount) {
            response.discount = discount;
            return this;
        }

        public Builder taxAmount(BigDecimal taxAmount) {
            response.taxAmount = taxAmount;
            return this;
        }

        public Builder netTotal(BigDecimal netTotal) {
            response.netTotal = netTotal;
            return this;
        }

        public Builder paidAmount(BigDecimal paidAmount) {
            response.paidAmount = paidAmount;
            return this;
        }

        public Builder changeAmount(BigDecimal changeAmount) {
            response.changeAmount = changeAmount;
            return this;
        }

        public Builder paymentStatus(String paymentStatus) {
            response.paymentStatus = paymentStatus;
            return this;
        }

        public Builder saleDate(LocalDateTime saleDate) {
            response.saleDate = saleDate;
            return this;
        }

        public Builder notes(String notes) {
            response.notes = notes;
            return this;
        }

        public Builder items(List<SaleItemResponse> items) {
            response.items = items;
            return this;
        }

        public Builder payments(List<PaymentResponse> payments) {
            response.payments = payments;
            return this;
        }

        public SaleResponse build() {
            return response;
        }
    }

    // All getters
    public Long getSaleId() { return saleId; }
    public String getInvoiceNo() { return invoiceNo; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public Long getCashierId() { return cashierId; }
    public String getCashierName() { return cashierName; }
    public Long getBranchId() { return branchId; }
    public String getBranchName() { return branchName; }
    public BigDecimal getGrossTotal() { return grossTotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getNetTotal() { return netTotal; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public BigDecimal getChangeAmount() { return changeAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public String getNotes() { return notes; }
    public List<SaleItemResponse> getItems() { return items; }
    public List<PaymentResponse> getPayments() { return payments; }
}