package com.nsbm.rocs.pos.service;

import com.nsbm.rocs.entity.pos.Payment;
import com.nsbm.rocs.entity.pos.Sale;
import com.nsbm.rocs.entity.pos.SaleItem;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.entity.inventory.Product;
import com.nsbm.rocs.pos.dto.sale.CreateSaleRequest;
import com.nsbm.rocs.pos.dto.sale.PaymentRequest;
import com.nsbm.rocs.pos.dto.sale.SaleItemRequest;
import com.nsbm.rocs.pos.dto.sale.SaleResponse;
import com.nsbm.rocs.pos.dto.sale.SaleItemResponse;
import com.nsbm.rocs.pos.dto.sale.PaymentResponse;
import com.nsbm.rocs.pos.dto.sale.SaleSummaryDTO;
import com.nsbm.rocs.pos.repository.CustomerRepository;
import com.nsbm.rocs.pos.repository.PaymentRepository;
import com.nsbm.rocs.pos.repository.SaleItemRepository;
import com.nsbm.rocs.pos.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PosService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public PosService(SaleRepository saleRepository,
                      SaleItemRepository saleItemRepository,
                      PaymentRepository paymentRepository,
                      CustomerRepository customerRepository,
                      ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request, Long branchId, Long cashierId, Long shiftId) {
        // 1. Create Sale Entity
        Sale sale = new Sale();
        sale.setInvoiceNo(generateInvoiceNo());
        sale.setBranchId(branchId);
        sale.setCashierId(cashierId);
        sale.setCustomerId(request.getCustomerId());
        sale.setShiftId(shiftId);
        sale.setSaleDate(LocalDateTime.now());
        sale.setNotes(request.getNotes());
        sale.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
        sale.setTaxAmount(BigDecimal.ZERO); // Calculate taxes if needed
        sale.setSaleType("RETAIL");

        // Determine status
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            sale.setPaymentStatus(request.getStatus().toUpperCase());
        } else {
             sale.setPaymentStatus("PAID"); // Default
        }

        // Calculate totals
        BigDecimal grossTotal = BigDecimal.ZERO;
        for (SaleItemRequest itemReq : request.getItems()) {
            BigDecimal unitPrice = itemReq.getUnitPrice();
            BigDecimal quantity = itemReq.getQuantity();

            // Handle null values - fetch from product if needed
            if (unitPrice == null || quantity == null) {
                Product product = productRepository.findById(itemReq.getProductId()).orElse(null);
                if (product != null) {
                    if (unitPrice == null) {
                        unitPrice = product.getSellingPrice() != null ? product.getSellingPrice() : BigDecimal.ZERO;
                    }
                    if (quantity == null) {
                        quantity = BigDecimal.ONE;
                    }
                } else {
                    unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
                    quantity = quantity != null ? quantity : BigDecimal.ONE;
                }
            }

            BigDecimal lineTotal = unitPrice.multiply(quantity);
            BigDecimal itemDiscount = itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO;
            lineTotal = lineTotal.subtract(itemDiscount);
            grossTotal = grossTotal.add(lineTotal);
        }
        sale.setGrossTotal(grossTotal);

        BigDecimal netTotal = grossTotal.subtract(sale.getDiscount());
        sale.setNetTotal(netTotal);

        BigDecimal paidAmount = BigDecimal.ZERO;
        if (request.getPayments() != null) {
            for(PaymentRequest pr : request.getPayments()) {
                BigDecimal amount = pr.getAmount() != null ? pr.getAmount() : BigDecimal.ZERO;
                paidAmount = paidAmount.add(amount);
            }
        }
        sale.setPaidAmount(paidAmount);
        sale.setChangeAmount(paidAmount.subtract(netTotal));

        if ("PAID".equals(sale.getPaymentStatus())) {
             if (paidAmount.compareTo(netTotal) < 0) {
                 sale.setPaymentStatus("PARTIAL");
             }
        }

        // Save Sale
        Long saleId = saleRepository.save(sale);
        sale.setSaleId(saleId);

        // 2. Save Items
        List<SaleItem> saleItems = new ArrayList<>();
        for (SaleItemRequest itemReq : request.getItems()) {
            BigDecimal unitPrice = itemReq.getUnitPrice();
            BigDecimal quantity = itemReq.getQuantity();

            // Handle null values - fetch from product if needed
            if (unitPrice == null) {
                Product product = productRepository.findById(itemReq.getProductId()).orElse(null);
                unitPrice = (product != null && product.getSellingPrice() != null)
                    ? product.getSellingPrice() : BigDecimal.ZERO;
            }
            if (quantity == null) {
                quantity = BigDecimal.ONE;
            }

            SaleItem item = new SaleItem();
            item.setSaleId(saleId);
            item.setProductId(itemReq.getProductId());
            item.setSerialId(itemReq.getSerialId());
            item.setQty(quantity);
            item.setUnitPrice(unitPrice);
            item.setDiscount(itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO);
            item.setTotal(unitPrice.multiply(quantity));
            // item.setTaxRate(...);

            saleItemRepository.save(item);
            saleItems.add(item);
        }

        // 3. Save Payments
        List<Payment> payments = new ArrayList<>();
        if (request.getPayments() != null) {
            for(PaymentRequest pr : request.getPayments()) {
                Payment payment = new Payment();
                payment.setSaleId(saleId);
                payment.setPaymentType(pr.getPaymentType() != null ? pr.getPaymentType() : "CASH");
                payment.setAmount(pr.getAmount() != null ? pr.getAmount() : BigDecimal.ZERO);
                payment.setReferenceNo(pr.getReferenceNo());
                payment.setCardLast4(pr.getCardLast4());
                payment.setBankName(pr.getBankName());

                paymentRepository.save(payment);
                payments.add(payment);
            }
        }

        return mapToResponse(sale, saleItems, payments);
    }

    /**
     * Get the last invoice number for today
     * @return Last invoice info with number and next sequence
     */
    public Map<String, Object> getLastInvoiceInfo() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String datePrefix = "INV-" + today;

        String lastInvoice = saleRepository.findLastInvoiceNoByDatePrefix(datePrefix);

        int nextSequence = 1;
        if (lastInvoice != null && lastInvoice.startsWith(datePrefix)) {
            try {
                // Extract the sequence number from INV-YYYYMMDD-XXXXX
                String[] parts = lastInvoice.split("-");
                if (parts.length >= 3) {
                    nextSequence = Integer.parseInt(parts[2]) + 1;
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

        String nextInvoiceNo = String.format("%s-%05d", datePrefix, nextSequence);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("lastInvoiceNo", lastInvoice);
        result.put("nextInvoiceNo", nextInvoiceNo);
        result.put("nextSequence", nextSequence);
        result.put("date", today);

        return result;
    }

    private String generateInvoiceNo() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String datePrefix = "INV-" + today;

        String lastInvoice = saleRepository.findLastInvoiceNoByDatePrefix(datePrefix);

        int nextSequence = 1;
        if (lastInvoice != null && lastInvoice.startsWith(datePrefix)) {
            try {
                String[] parts = lastInvoice.split("-");
                if (parts.length >= 3) {
                    nextSequence = Integer.parseInt(parts[2]) + 1;
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return String.format("%s-%05d", datePrefix, nextSequence);
    }

    private SaleResponse mapToResponse(Sale sale, List<SaleItem> items, List<Payment> payments) {
        SaleResponse.Builder builder = new SaleResponse.Builder()
                .saleId(sale.getSaleId())
                .invoiceNo(sale.getInvoiceNo())
                .customerId(sale.getCustomerId())
                .grossTotal(sale.getGrossTotal())
                .discount(sale.getDiscount())
                .taxAmount(sale.getTaxAmount())
                .netTotal(sale.getNetTotal())
                .paidAmount(sale.getPaidAmount())
                .changeAmount(sale.getChangeAmount())
                .paymentStatus(sale.getPaymentStatus())
                .saleDate(sale.getSaleDate())
                .notes(sale.getNotes());

        // Fetch Customer Name
        if (sale.getCustomerId() != null) {
            customerRepository.findById(sale.getCustomerId())
                    .ifPresent(customer -> builder.customerName(customer.getName()));
        }

        // Fetch Product Names optimization
        List<Long> productIds = items.stream().map(SaleItem::getProductId).collect(Collectors.toList());
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        List<SaleItemResponse> itemResponses = items.stream().map(item -> {
            SaleItemResponse res = new SaleItemResponse();
            res.setSaleItemId(item.getSaleItemId()); // Ensure ID is set
            res.setProductId(item.getProductId());

            Product product = productMap.get(item.getProductId());
            if (product != null) {
                res.setProductName(product.getName());
                // res.setSku(product.getSku());
                // res.setBarcode(product.getBarcode());
            }

            res.setQuantity(item.getQty());
            res.setUnitPrice(item.getUnitPrice());
            res.setTotal(item.getTotal());
            return res;
        }).collect(Collectors.toList());
        builder.items(itemResponses);

        List<PaymentResponse> paymentResponses = payments.stream().map(p -> {
            PaymentResponse res = new PaymentResponse();
            res.setPaymentType(p.getPaymentType());
            res.setAmount(p.getAmount());
            return res;
        }).collect(Collectors.toList());
        builder.payments(paymentResponses);

        return builder.build();
    }

    public List<SaleResponse> getSales(String status) {
        List<Sale> sales;
        if (status != null && !status.isEmpty()) {
             sales = saleRepository.findByPaymentStatus(status);
        } else {
            sales = saleRepository.findAll();
        }

        return sales.stream().map(sale -> {
            List<SaleItem> items = saleItemRepository.findBySaleId(sale.getSaleId());
            List<Payment> payments = paymentRepository.findBySaleId(sale.getSaleId());
            return mapToResponse(sale, items, payments);
        }).collect(Collectors.toList());
    }

    public SaleResponse getSaleById(Long id) {
       // Implementation for getBillById
        return saleRepository.findById(id).map(sale -> {
            List<SaleItem> items = saleItemRepository.findBySaleId(id);
            List<Payment> payments = paymentRepository.findBySaleId(id);
            return mapToResponse(sale, items, payments);
        }).orElse(null);
    }

    public List<SaleSummaryDTO> getSaleSummaries(String status) {
        // Map frontend status to backend status
        String dbStatus = status;
        if ("COMPLETED".equalsIgnoreCase(status)) {
            dbStatus = "PAID";
        } else if ("RECALL".equalsIgnoreCase(status)) {
            dbStatus = "HELD";
        }

        List<Sale> sales;
        if (dbStatus != null && !dbStatus.isEmpty()) {
             sales = saleRepository.findByPaymentStatus(dbStatus);
        } else {
            sales = saleRepository.findAll();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return sales.stream().map(sale -> {
            // Optimization: count items instead of fetching all if repository supports it
            // For now, fetch list to get count
            List<SaleItem> items = saleItemRepository.findBySaleId(sale.getSaleId());

            return new SaleSummaryDTO(
                sale.getInvoiceNo(),
                sale.getSaleDate() != null ? sale.getSaleDate().format(formatter) : "",
                items.size(),
                sale.getNetTotal(),
                sale.getPaymentStatus()
            );
        }).collect(Collectors.toList());
    }
}
