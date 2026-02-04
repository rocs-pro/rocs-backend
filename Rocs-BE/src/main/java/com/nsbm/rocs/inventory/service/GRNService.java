package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.entity.inventory.*;
import com.nsbm.rocs.inventory.dto.*;
import com.nsbm.rocs.inventory.exception.GRNException;
import com.nsbm.rocs.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GRNService {

    @Autowired
    private GRNRepository grnRepository;

    @Autowired
    private GRNItemRepository grnItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BatchRepository batchRepository;

    /**
     * Create a new GRN
     */
    public GRNResponseDTO createGRN(GRNCreateRequestDTO request, Long currentUserId) {
        // Validate supplier exists
        if (!supplierRepository.existsById(request.getSupplierId())) {
            throw new GRNException("Supplier not found with ID: " + request.getSupplierId());
        }

        // Validate all products exist
        for (GRNCreateRequestDTO.GRNItemCreateDTO item : request.getItems()) {
            if (!productRepository.existsById(item.getProductId())) {
                throw new GRNException("Product not found with ID: " + item.getProductId());
            }
        }

        // Create GRN
        GRN grn = new GRN();
        grn.setGrnNo(generateGRNNumber(request.getBranchId()));
        grn.setBranchId(request.getBranchId());
        grn.setSupplierId(request.getSupplierId());
        grn.setPoId(request.getPoId());
        grn.setGrnDate(request.getGrnDate());
        grn.setInvoiceNo(request.getInvoiceNo());
        grn.setInvoiceDate(request.getInvoiceDate());
        grn.setStatus("PENDING");
        grn.setPaymentStatus("UNPAID");
        grn.setCreatedBy(currentUserId);

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (GRNCreateRequestDTO.GRNItemCreateDTO item : request.getItems()) {
            BigDecimal itemTotal = item.getQtyReceived().multiply(item.getUnitPrice());
            totalAmount = totalAmount.add(itemTotal);
        }

        grn.setTotalAmount(totalAmount);
        grn.setNetAmount(totalAmount);

        // Save GRN
        grn = grnRepository.save(grn);

        // Create GRN Items
        for (GRNCreateRequestDTO.GRNItemCreateDTO itemDTO : request.getItems()) {
            GRNItem item = new GRNItem();
            item.setGrnId(grn.getGrnId());
            item.setProductId(itemDTO.getProductId());
            item.setBatchCode(itemDTO.getBatchCode());
            item.setExpiryDate(itemDTO.getExpiryDate());
            item.setQtyReceived(itemDTO.getQtyReceived());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTotal(itemDTO.getQtyReceived().multiply(itemDTO.getUnitPrice()));

            grnItemRepository.save(item);
        }

        return getGRNById(grn.getGrnId());
    }

    /**
     * Approve a GRN and update stock
     */
    public GRNResponseDTO approveGRN(Long grnId, Long approvedBy) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("GRN is not in pending status");
        }

        // Update GRN status
        grn.setStatus("APPROVED");
        grn.setApprovedBy(approvedBy);
        grnRepository.save(grn);

        // Update stock for each item
        List<GRNItem> grnItems = grnItemRepository.findByGrnId(grnId);
        for (GRNItem item : grnItems) {
            updateStock(grn.getBranchId(), item);

            // Create batch if batch code is provided
            if (item.getBatchCode() != null && !item.getBatchCode().trim().isEmpty()) {
                createBatch(grn.getBranchId(), item);
            }
        }

        return getGRNById(grnId);
    }

    /**
     * Get GRN by ID with full details
     */
    public GRNResponseDTO getGRNById(Long grnId) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        List<GRNItem> grnItems = grnItemRepository.findByGrnId(grnId);

        return mapToResponseDTO(grn, grnItems);
    }

    /**
     * Get all GRNs for a branch
     */
    public List<GRNResponseDTO> getGRNsByBranch(Long branchId) {
        List<GRN> grns = grnRepository.findByBranchId(branchId);
        return grns.stream()
                .map(grn -> {
                    List<GRNItem> items = grnItemRepository.findByGrnId(grn.getGrnId());
                    return mapToResponseDTO(grn, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Search GRNs with filters
     */
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
                .map(grn -> {
                    List<GRNItem> items = grnItemRepository.findByGrnId(grn.getGrnId());
                    return mapToResponseDTO(grn, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Update GRN payment status
     */
    public GRNResponseDTO updatePaymentStatus(Long grnId, String paymentStatus) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!List.of("UNPAID", "PARTIALLY_PAID", "PAID").contains(paymentStatus)) {
            throw new GRNException("Invalid payment status: " + paymentStatus);
        }

        grn.setPaymentStatus(paymentStatus);
        grnRepository.save(grn);

        return getGRNById(grnId);
    }

    /**
     * Delete GRN (only if pending)
     */
    public void deleteGRN(Long grnId) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("Cannot delete GRN that is not in pending status");
        }

        // Delete GRN items first
        grnItemRepository.deleteAll(grnItemRepository.findByGrnId(grnId));

        // Delete GRN
        grnRepository.delete(grn);
    }

    /**
     * Update GRN (only if pending)
     */
    public GRNResponseDTO updateGRN(Long grnId, GRNUpdateRequestDTO request) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("Cannot update GRN that is not in pending status");
        }

        // Update basic fields
        if (request.getGrnDate() != null) {
            grn.setGrnDate(request.getGrnDate());
        }
        if (request.getInvoiceNo() != null) {
            grn.setInvoiceNo(request.getInvoiceNo());
        }
        if (request.getInvoiceDate() != null) {
            grn.setInvoiceDate(request.getInvoiceDate());
        }

        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Validate all products exist
            for (GRNCreateRequestDTO.GRNItemCreateDTO item : request.getItems()) {
                if (!productRepository.existsById(item.getProductId())) {
                    throw new GRNException("Product not found with ID: " + item.getProductId());
                }
            }

            // Delete existing items
            grnItemRepository.deleteAll(grnItemRepository.findByGrnId(grnId));

            // Create new items
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (GRNCreateRequestDTO.GRNItemCreateDTO itemDTO : request.getItems()) {
                GRNItem item = new GRNItem();
                item.setGrnId(grnId);
                item.setProductId(itemDTO.getProductId());
                item.setBatchCode(itemDTO.getBatchCode());
                item.setExpiryDate(itemDTO.getExpiryDate());
                item.setQtyReceived(itemDTO.getQtyReceived());
                item.setUnitPrice(itemDTO.getUnitPrice());
                item.setTotal(itemDTO.getQtyReceived().multiply(itemDTO.getUnitPrice()));

                grnItemRepository.save(item);
                totalAmount = totalAmount.add(item.getTotal());
            }

            // Update totals
            grn.setTotalAmount(totalAmount);
            grn.setNetAmount(totalAmount);
        }

        grnRepository.save(grn);
        return getGRNById(grnId);
    }

    /**
     * Check if GRN number exists
     */
    private String generateGRNNumber(Long branchId) {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Long count = grnRepository.countByBranchIdAndGrnDate(branchId, today);
        String sequence = String.format("%03d", count + 1);

        return String.format("GRN-%d-%s-%s", branchId, dateStr, sequence);
    }

    /**
     * Update stock when GRN is approved
     */
    private void updateStock(Long branchId, GRNItem grnItem) {
        Optional<Stock> existingStock = stockRepository.findByBranchIdAndProductId(branchId, grnItem.getProductId());

        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity().add(grnItem.getQtyReceived()));
            stock.setAvailableQty(stock.getAvailableQty().add(grnItem.getQtyReceived()));
            stockRepository.save(stock);
        } else {
            Stock newStock = new Stock();
            newStock.setBranchId(branchId);
            newStock.setProductId(grnItem.getProductId());
            newStock.setQuantity(grnItem.getQtyReceived());
            newStock.setReservedQty(BigDecimal.ZERO);
            newStock.setAvailableQty(grnItem.getQtyReceived());
            stockRepository.save(newStock);
        }
    }

    /**
     * Create batch record
     */
    private void createBatch(Long branchId, GRNItem grnItem) {
        Batch batch = new Batch();
        batch.setProductId(grnItem.getProductId());
        batch.setBranchId(branchId);
        batch.setBatchCode(grnItem.getBatchCode());
        batch.setExpiryDate(grnItem.getExpiryDate());
        batch.setQty(grnItem.getQtyReceived());
        batch.setCostPrice(grnItem.getUnitPrice());

        batchRepository.save(batch);
    }

    /**
     * Map GRN entity to response DTO
     */
    private GRNResponseDTO mapToResponseDTO(GRN grn, List<GRNItem> grnItems) {
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

        // Map GRN items
        List<GRNItemDTO> itemDTOs = grnItems.stream()
                .map(item -> {
                    GRNItemDTO itemDTO = new GRNItemDTO();
                    itemDTO.setGrnItemId(item.getGrnItemId());
                    itemDTO.setGrnId(item.getGrnId());
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setBatchCode(item.getBatchCode());
                    itemDTO.setExpiryDate(item.getExpiryDate());
                    itemDTO.setQtyReceived(item.getQtyReceived());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTO.setTotal(item.getTotal());

                    // Get product name and SKU
                    productRepository.findById(item.getProductId()).ifPresent(product -> {
                        itemDTO.setProductName(product.getName());
                        itemDTO.setProductSku(product.getSku());
                    });

                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);

        // Get supplier name
        supplierRepository.findById(grn.getSupplierId()).ifPresent(supplier -> {
            dto.setSupplierName(supplier.getName());
        });

        return dto;
    }

    /**
     * Get GRN statistics for a branch
     */
    public GRNStatsDTO getGRNStats(Long branchId, String period) {
        GRNStatsDTO stats = new GRNStatsDTO();

        // Get GRNs based on branch filter
        List<GRN> allGRNs = branchId != null ?
            grnRepository.findByBranchId(branchId) :
            grnRepository.findAll();

        // Basic counts
        stats.setTotalGRNs((long) allGRNs.size());
        stats.setPendingGRNs(allGRNs.stream()
            .filter(grn -> "PENDING".equals(grn.getStatus()))
            .count());
        stats.setApprovedGRNs(allGRNs.stream()
            .filter(grn -> "APPROVED".equals(grn.getStatus()))
            .count());

        // Financial stats
        stats.setTotalValue(allGRNs.stream()
            .map(grn -> grn.getTotalAmount() != null ? grn.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setUnpaidAmount(allGRNs.stream()
            .filter(grn -> "UNPAID".equals(grn.getPaymentStatus()))
            .map(grn -> grn.getTotalAmount() != null ? grn.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setPaidAmount(allGRNs.stream()
            .filter(grn -> "PAID".equals(grn.getPaymentStatus()))
            .map(grn -> grn.getTotalAmount() != null ? grn.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Item stats
        List<GRNItem> allItems = allGRNs.stream()
            .flatMap(grn -> grnItemRepository.findByGrnId(grn.getGrnId()).stream())
            .collect(Collectors.toList());

        stats.setTotalItems((long) allItems.size());
        stats.setUniqueProducts(allItems.stream()
            .map(GRNItem::getProductId)
            .distinct()
            .count());

        // Supplier stats
        stats.setActiveSuppliers(allGRNs.stream()
            .map(GRN::getSupplierId)
            .distinct()
            .count());

        return stats;
    }

    /**
     * Reject a GRN
     */
    public GRNResponseDTO rejectGRN(Long grnId, Long rejectedBy, String reason) {
        GRN grn = grnRepository.findById(grnId)
                .orElseThrow(() -> new GRNException("GRN not found with ID: " + grnId));

        if (!"PENDING".equals(grn.getStatus())) {
            throw new GRNException("GRN is not in pending status");
        }

        grn.setStatus("REJECTED");
        grn.setApprovedBy(rejectedBy); // Using same field for rejected by
        grnRepository.save(grn);

        return getGRNById(grnId);
    }

    /**
     * Get GRN items by product
     */
    public List<GRNItemDTO> getGRNItemsByProduct(Long productId, Long branchId) {
        List<GRNItem> items;
        if (branchId != null) {
            items = grnItemRepository.findByBranchIdAndProductId(branchId, productId);
        } else {
            items = grnItemRepository.findByProductId(productId);
        }

        return items.stream()
                .map(item -> {
                    GRNItemDTO dto = new GRNItemDTO();
                    dto.setGrnItemId(item.getGrnItemId());
                    dto.setGrnId(item.getGrnId());
                    dto.setProductId(item.getProductId());
                    dto.setBatchCode(item.getBatchCode());
                    dto.setExpiryDate(item.getExpiryDate());
                    dto.setQtyReceived(item.getQtyReceived());
                    dto.setUnitPrice(item.getUnitPrice());
                    dto.setTotal(item.getTotal());

                    // Get product details
                    productRepository.findById(item.getProductId()).ifPresent(product -> {
                        dto.setProductName(product.getName());
                        dto.setProductSku(product.getSku());
                    });

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if GRN number exists
     */
    public boolean isGRNNumberExists(String grnNo) {
        return grnRepository.findByGrnNo(grnNo).isPresent();
    }
}
