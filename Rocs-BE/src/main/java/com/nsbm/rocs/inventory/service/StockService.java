package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.StockAdjustmentDTO;
import com.nsbm.rocs.inventory.dto.StockDTO;
import com.nsbm.rocs.inventory.dto.StockReportDTO;
import com.nsbm.rocs.inventory.dto.LowStockAlertDTO;
import com.nsbm.rocs.inventory.entity.Product;
import com.nsbm.rocs.inventory.entity.Stock;
import com.nsbm.rocs.inventory.exception.InsufficientStockException;
import com.nsbm.rocs.inventory.exception.ResourceNotFoundException;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.inventory.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    public List<StockDTO> getAllStock() {
        return stockRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StockDTO> getStockByBranch(Long branchId) {
        return stockRepository.findByBranchId(branchId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StockDTO> getStockByProduct(Long productId) {
        return stockRepository.findByProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StockDTO getStockByBranchAndProduct(Long branchId, Long productId) {
        Stock stock = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for branch " + branchId + " and product " + productId));
        return convertToDTO(stock);
    }

    @Transactional
    public StockDTO adjustStock(StockAdjustmentDTO adjustmentDTO) {
        if (adjustmentDTO.getBranchId() == null || adjustmentDTO.getProductId() == null) {
            throw new IllegalArgumentException("branchId and productId are required");
        }
        Integer delta = adjustmentDTO.getAdjustmentQty();
        if (delta == null || delta == 0) {
            throw new IllegalArgumentException("adjustmentQty must be non-zero");
        }

        Stock stock = stockRepository.findStockForUpdate(adjustmentDTO.getBranchId(), adjustmentDTO.getProductId())
                .orElseGet(() -> stockRepository.save(newStock(adjustmentDTO.getBranchId(), adjustmentDTO.getProductId())));

        if (delta > 0) {
            stockRepository.incrementQuantity(adjustmentDTO.getBranchId(), adjustmentDTO.getProductId(), delta);
        } else {
            int updated = stockRepository.decrementQuantityIfAvailable(adjustmentDTO.getBranchId(), adjustmentDTO.getProductId(), Math.abs(delta));
            if (updated == 0) {
                throw new InsufficientStockException("Insufficient available stock to deduct " + Math.abs(delta));
            }
        }

        Stock refreshed = stockRepository.findByBranchIdAndProductId(adjustmentDTO.getBranchId(), adjustmentDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found after adjust"));
        return convertToDTO(refreshed);
    }

    @Transactional
    public StockDTO addStock(Long branchId, Long productId, Integer quantity) {
        validateStockInput(branchId, productId, quantity);

        stockRepository.findStockForUpdate(branchId, productId)
                .orElseGet(() -> stockRepository.save(newStock(branchId, productId)));

        stockRepository.incrementQuantity(branchId, productId, quantity);
        Stock refreshed = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found after add"));
        return convertToDTO(refreshed);
    }

    @Transactional
    public StockDTO removeStock(Long branchId, Long productId, Integer quantity) {
        validateStockInput(branchId, productId, quantity);

        stockRepository.findStockForUpdate(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for branch " + branchId + " and product " + productId));

        int updated = stockRepository.decrementQuantityIfAvailable(branchId, productId, quantity);
        if (updated == 0) {
            throw new InsufficientStockException("Insufficient stock");
        }
        Stock refreshed = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found after remove"));
        return convertToDTO(refreshed);
    }

    @Transactional
    public StockDTO reserveStock(Long branchId, Long productId, Integer quantity) {
        validateStockInput(branchId, productId, quantity);

        stockRepository.findStockForUpdate(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for branch " + branchId + " and product " + productId));

        int updated = stockRepository.incrementReservedIfAvailable(branchId, productId, quantity);
        if (updated == 0) {
            throw new InsufficientStockException("Insufficient stock");
        }
        Stock refreshed = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found after reserve"));
        return convertToDTO(refreshed);
    }

    @Transactional
    public StockDTO releaseReservedStock(Long branchId, Long productId, Integer quantity) {
        validateStockInput(branchId, productId, quantity);

        stockRepository.findStockForUpdate(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for branch " + branchId + " and product " + productId));

        stockRepository.decrementReserved(branchId, productId, quantity);
        Stock refreshed = stockRepository.findByBranchIdAndProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found after release"));
        return convertToDTO(refreshed);
    }

    public List<StockReportDTO> getStockReport(Long branchId) {
        List<Stock> stocks = branchId != null
                ? stockRepository.findByBranchId(branchId)
                : stockRepository.findAll();

        List<StockReportDTO> reports = new ArrayList<>();

        for (Stock stock : stocks) {
            Product product = productRepository.findById(stock.getProductId()).orElse(null);
            if (product == null) continue;

            StockReportDTO report = new StockReportDTO();
            report.setProductId(product.getProductId());
            report.setSku(product.getSku());
            report.setProductName(product.getName());
            report.setTotalStock(stock.getQuantity());
            report.setReservedQty(stock.getReservedQty());
            report.setAvailableQty(stock.getAvailableQty());
            report.setReorderLevel(product.getReorderLevel());

            // Calculate stock value
            report.setCostValue(product.getCostPrice().multiply(BigDecimal.valueOf(stock.getQuantity())));
            report.setSellingValue(product.getSellingPrice().multiply(BigDecimal.valueOf(stock.getQuantity())));

            // Determine stock status
            if (stock.getQuantity() == 0) {
                report.setStockStatus("OUT_OF_STOCK");
            } else if (stock.getQuantity() <= product.getReorderLevel()) {
                report.setStockStatus("LOW_STOCK");
            } else if (product.getMaxStockLevel() > 0 && stock.getQuantity() > product.getMaxStockLevel()) {
                report.setStockStatus("OVERSTOCKED");
            } else {
                report.setStockStatus("IN_STOCK");
            }

            reports.add(report);
        }

        return reports;
    }

    public List<LowStockAlertDTO> getLowStockAlerts(Long branchId) {
        List<Stock> stocks = branchId != null
                ? stockRepository.findByBranchId(branchId)
                : stockRepository.findAll();

        List<LowStockAlertDTO> alerts = new ArrayList<>();

        for (Stock stock : stocks) {
            Product product = productRepository.findById(stock.getProductId()).orElse(null);
            if (product == null) continue;

            if (stock.getQuantity() <= product.getReorderLevel()) {
                LowStockAlertDTO alert = new LowStockAlertDTO();
                alert.setProductId(product.getProductId());
                alert.setSku(product.getSku());
                alert.setProductName(product.getName());
                alert.setBranchId(stock.getBranchId());
                alert.setCurrentStock(stock.getQuantity());
                alert.setReorderLevel(product.getReorderLevel());
                alert.setShortfall(product.getReorderLevel() - stock.getQuantity());

                if (stock.getQuantity() == 0) {
                    alert.setAlertLevel("CRITICAL");
                    alert.setMessage("Out of stock");
                } else {
                    alert.setAlertLevel("WARNING");
                    alert.setMessage("Low stock: below reorder level");
                }

                alerts.add(alert);
            }
        }

        return alerts;
    }

    private void validateStockInput(Long branchId, Long productId, Integer quantity) {
        if (branchId == null || productId == null) {
            throw new IllegalArgumentException("branchId and productId are required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }

    private Stock newStock(Long branchId, Long productId) {
        Stock newStock = new Stock();
        newStock.setBranchId(branchId);
        newStock.setProductId(productId);
        newStock.setQuantity(0);
        newStock.setReservedQty(0);
        return newStock;
    }

    private StockDTO convertToDTO(Stock stock) {
        StockDTO dto = new StockDTO();
        dto.setStockId(stock.getStockId());
        dto.setBranchId(stock.getBranchId());
        dto.setProductId(stock.getProductId());
        dto.setQuantity(stock.getQuantity());
        dto.setReservedQty(stock.getReservedQty());
        dto.setAvailableQty(stock.getAvailableQty());
        dto.setLastUpdated(stock.getLastUpdated());

        // Set product details
        productRepository.findById(stock.getProductId()).ifPresent(product -> {
            dto.setProductName(product.getName());
            dto.setProductSku(product.getSku());
        });

        return dto;
    }
}

