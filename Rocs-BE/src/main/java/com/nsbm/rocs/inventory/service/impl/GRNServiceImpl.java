package com.nsbm.rocs.inventory.service.impl;

import com.nsbm.rocs.entity.inventory.GRN;
import com.nsbm.rocs.entity.inventory.GRNItem;
import com.nsbm.rocs.entity.inventory.Supplier;
import com.nsbm.rocs.entity.inventory.Stock;
import com.nsbm.rocs.entity.inventory.Batch;
import com.nsbm.rocs.inventory.dto.*;
import com.nsbm.rocs.inventory.exception.GRNException;
import com.nsbm.rocs.inventory.repository.GRNRepository;
import com.nsbm.rocs.inventory.repository.GRNItemRepository;
import com.nsbm.rocs.inventory.repository.SupplierRepository;
import com.nsbm.rocs.inventory.repository.InventoryStockRepository;
import com.nsbm.rocs.inventory.repository.BatchRepository;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.repository.BranchRepository;
import com.nsbm.rocs.inventory.service.GRNService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GRNServiceImpl implements GRNService {

    @Autowired
    private GRNRepository grnRepository;

    @Autowired
    private GRNItemRepository grnItemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryStockRepository stockRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public GRNResponseDTO createGRN(GRNCreateRequestDTO request, Long currentUserId) {
        log.info("Creating GRN for branch: {}, supplier: {}", request.getBranchId(), request.getSupplierId());

        // Validate supplier exists and is active
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new GRNException("Supplier not found with ID: " + request.getSupplierId()));

        if (!supplier.getIsActive()) {
            throw new GRNException("Cannot create GRN for inactive supplier");
        }

        // Generate GRN number
        String grnNo = generateGRNNumber(request.getBranchId());

        // Create GRN
        GRN grn = new GRN();
        grn.setGrnNo(grnNo);
        grn.setBranchId(request.getBranchId());
        grn.setSupplierId(request.getSupplierId());
        grn.setPoId(request.getPoId());
        grn.setGrnDate(request.getGrnDate());
        grn.setInvoiceNo(request.getInvoiceNo());
        grn.setInvoiceDate(request.getInvoiceDate());
        grn.setCreatedBy(currentUserId);
        grn.setStatus("PENDING");
        grn.setPaymentStatus("UNPAID");

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (GRNCreateRequestDTO.GRNItemCreateDTO itemDto : request.getItems()) {
            BigDecimal itemTotal = itemDto.getQtyReceived().multiply(itemDto.getUnitPrice());
            totalAmount = totalAmount.add(itemTotal);
        }

        grn.setTotalAmount(totalAmount);
        grn.setNetAmount(totalAmount);

        // Save GRN
        grn = grnRepository.save(grn);

        // Create GRN Items
        for (GRNCreateRequestDTO.GRNItemCreateDTO itemDto : request.getItems()) {
            // Validate product exists
            productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new GRNException("Product not found with ID: " + itemDto.getProductId()));

            GRNItem grnItem = new GRNItem();
            grnItem.setGrnId(grn.getGrnId());
            grnItem.setProductId(itemDto.getProductId());
            grnItem.setBatchCode(itemDto.getBatchCode());
            grnItem.setExpiryDate(itemDto.getExpiryDate());
            grnItem.setQtyReceived(itemDto.getQtyReceived());
            grnItem.setUnitPrice(itemDto.getUnitPrice());
            grnItem.setTotal(itemDto.getQtyReceived().multiply(itemDto.getUnitPrice()));

            grnItemRepository.save(grnItem);
        }

        log.info("GRN created successfully with number: {}", grnNo);
        return convertToResponseDTO(grn);
    }

    @Override
    public GRNResponseDTO getGRNById(Long grnId) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));
        return convertToResponseDTO(grn);
    }

    @Override
    public List<GRNResponseDTO> getGRNsByBranch(Long branchId) {
        List<GRN> grns = grnRepository.findByBranchId(branchId);
        return grns.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GRNResponseDTO> searchGRNs(GRNFilterDTO filter) {
        List<GRN> grns = grnRepository.findByFilters(
                filter.getBranchId(),
                filter.getSupplierId(),
                filter.getStatus(),
                filter.getPaymentStatus(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getGrnNo(),
                filter.getInvoiceNo()
        );

        return grns.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GRNResponseDTO updateGRN(Long grnId, GRNUpdateRequestDTO request) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("Cannot update GRN that is not in PENDING status");
        }

        // Update GRN fields
        if (request.getInvoiceNo() != null) {
            grn.setInvoiceNo(request.getInvoiceNo());
        }
        if (request.getInvoiceDate() != null) {
            grn.setInvoiceDate(request.getInvoiceDate());
        }

        grn = grnRepository.save(grn);
        log.info("GRN updated: {}", grnId);

        return convertToResponseDTO(grn);
    }

    @Override
    public GRNResponseDTO approveGRN(Long grnId, Long approvedBy) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("Can only approve GRNs in PENDING status");
        }

        // Update stock levels
        updateStockFromGRN(grn);

        // Update GRN status
        grn.setStatus("APPROVED");
        grn.setApprovedBy(approvedBy);
        grn = grnRepository.save(grn);

        log.info("GRN approved: {}", grnId);
        return convertToResponseDTO(grn);
    }

    @Override
    public GRNResponseDTO rejectGRN(Long grnId, Long rejectedBy, String reason) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("Can only reject GRNs in PENDING status");
        }

        grn.setStatus("REJECTED");
        grn.setApprovedBy(rejectedBy);
        grn = grnRepository.save(grn);

        log.info("GRN rejected: {} by user: {}", grnId, rejectedBy);
        return convertToResponseDTO(grn);
    }

    @Override
    public GRNResponseDTO updatePaymentStatus(Long grnId, String paymentStatus) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"APPROVED".equals(grn.getStatus())) {
            throw new GRNException("Can only update payment status for approved GRNs");
        }

        grn.setPaymentStatus(paymentStatus);
        grn = grnRepository.save(grn);

        log.info("GRN payment status updated: {} to {}", grnId, paymentStatus);
        return convertToResponseDTO(grn);
    }

    @Override
    public void deleteGRN(Long grnId) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("Can only delete GRNs in PENDING status");
        }

        // Delete GRN items first
        grnItemRepository.deleteAll(grnItemRepository.findByGrnId(grnId));

        // Delete GRN
        grnRepository.delete(grn);

        log.info("GRN deleted: {}", grnId);
    }

    @Override
    public GRNStatsDTO getGRNStats(Long branchId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        startDate = switch (period.toLowerCase()) {
            case "week" -> endDate.minusWeeks(1);
            case "month" -> endDate.minusMonths(1);
            case "quarter" -> endDate.minusMonths(3);
            case "year" -> endDate.minusYears(1);
            default -> endDate.minusMonths(1); // Default to month
        };

        List<GRN> grns = grnRepository.findByBranchIdAndGrnDateBetween(branchId, startDate, endDate);

        GRNStatsDTO stats = new GRNStatsDTO();
        stats.setPeriod(period);
        stats.setStartDate(startDate);
        stats.setEndDate(endDate);
        stats.setTotalGRNs((long) grns.size());
        stats.setPendingGRNs(grns.stream().filter(g -> "PENDING".equals(g.getStatus())).count());
        stats.setApprovedGRNs(grns.stream().filter(g -> "APPROVED".equals(g.getStatus())).count());
        stats.setRejectedGRNs(grns.stream().filter(g -> "REJECTED".equals(g.getStatus())).count());

        BigDecimal totalValue = grns.stream()
                .filter(g -> "APPROVED".equals(g.getStatus()))
                .map(GRN::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalValue(totalValue);

        return stats;
    }

    @Override
    public List<GRNItemDTO> getGRNItemsByProduct(Long productId, Long branchId) {
        List<GRNItem> items = grnItemRepository.findByBranchIdAndProductId(branchId, productId);
        return items.stream()
                .map(this::convertToGRNItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isGRNNumberExists(String grnNo) {
        return grnRepository.findByGrnNo(grnNo).isPresent();
    }

    // Helper methods

    private String generateGRNNumber(Long branchId) {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Long count = grnRepository.countByBranchIdAndGrnDate(branchId, today);
        count++; // Start from 1

        String sequence = String.format("%03d", count);
        return String.format("GRN-%d-%s-%s", branchId, dateStr, sequence);
    }

    private void updateStockFromGRN(GRN grn) {
        List<GRNItem> items = grnItemRepository.findByGrnId(grn.getGrnId());

        for (GRNItem item : items) {
            Optional<Stock> stockOpt = stockRepository.findByBranchIdAndProductId(grn.getBranchId(), item.getProductId());

            Stock stock;
            if (stockOpt.isPresent()) {
                stock = stockOpt.get();
                stock.setQuantity(stock.getQuantity().add(item.getQtyReceived()));
                stock.setAvailableQty(stock.getAvailableQty().add(item.getQtyReceived()));
            } else {
                stock = new Stock();
                stock.setBranchId(grn.getBranchId());
                stock.setProductId(item.getProductId());
                stock.setQuantity(item.getQtyReceived());
                stock.setReservedQty(BigDecimal.ZERO);
                stock.setAvailableQty(item.getQtyReceived());
            }

            stockRepository.save(stock);

            // Create batch if batch code is provided
            if (item.getBatchCode() != null && !item.getBatchCode().trim().isEmpty()) {
                createBatch(grn.getBranchId(), item);
            }
        }
    }

    private void createBatch(Long branchId, GRNItem item) {
        Batch batch = new Batch();
        batch.setProductId(item.getProductId());
        batch.setBranchId(branchId);
        batch.setBatchCode(item.getBatchCode());
        batch.setExpiryDate(item.getExpiryDate());
        batch.setQty(item.getQtyReceived());
        batch.setCostPrice(item.getUnitPrice());
        batch.setCreatedAt(LocalDateTime.now());

        batchRepository.save(batch);
    }

    private GRNResponseDTO convertToResponseDTO(GRN grn) {
        GRNResponseDTO dto = new GRNResponseDTO();
        dto.setGrnId(grn.getGrnId());
        dto.setGrnNo(grn.getGrnNo());
        dto.setBranchId(grn.getBranchId());
        dto.setSupplierId(grn.getSupplierId());
        dto.setPoId(grn.getPoId());
        dto.setGrnDate(grn.getGrnDate());
        dto.setInvoiceNo(grn.getInvoiceNo());
        dto.setInvoiceDate(grn.getInvoiceDate());
        dto.setTotalAmount(grn.getTotalAmount());
        dto.setNetAmount(grn.getNetAmount());
        dto.setPaymentStatus(grn.getPaymentStatus());
        dto.setStatus(grn.getStatus());
        dto.setCreatedBy(grn.getCreatedBy());
        dto.setApprovedBy(grn.getApprovedBy());
        dto.setCreatedAt(grn.getCreatedAt());

        // Set supplier name
        supplierRepository.findById(grn.getSupplierId()).ifPresent(supplier ->
            dto.setSupplierName(supplier.getName()));

        // Set branch name
        branchRepository.findById(grn.getBranchId()).ifPresent(branch ->
            dto.setBranchName(branch.getName()));

        // Get GRN items
        List<GRNItem> items = grnItemRepository.findByGrnId(grn.getGrnId());
        List<GRNItemDTO> itemDTOs = items.stream()
                .map(this::convertToGRNItemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    private GRNItemDTO convertToGRNItemDTO(GRNItem item) {
        GRNItemDTO dto = new GRNItemDTO();
        dto.setGrnItemId(item.getGrnItemId());
        dto.setGrnId(item.getGrnId());
        dto.setProductId(item.getProductId());
        dto.setBatchCode(item.getBatchCode());
        dto.setExpiryDate(item.getExpiryDate());
        dto.setQtyReceived(item.getQtyReceived());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotal(item.getTotal());

        // Set product details
        productRepository.findById(item.getProductId()).ifPresent(product -> {
            dto.setProductName(product.getName());
            dto.setProductSku(product.getSku());
        });

        return dto;
    }
}

