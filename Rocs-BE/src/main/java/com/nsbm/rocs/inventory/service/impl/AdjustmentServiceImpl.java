package com.nsbm.rocs.inventory.service.impl;

import com.nsbm.rocs.entity.inventory.Stock;
import com.nsbm.rocs.inventory.dto.StockAdjustmentDTO;
import com.nsbm.rocs.inventory.exception.InsufficientStockException;
import com.nsbm.rocs.inventory.exception.ResourceNotFoundException;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.inventory.repository.StockRepository;
import com.nsbm.rocs.inventory.service.AdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdjustmentServiceImpl implements AdjustmentService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final List<StockAdjustmentDTO> adjustmentHistory = new ArrayList<>();

    @Override
    @Transactional(readOnly = true)
    public List<StockAdjustmentDTO> getAllAdjustments(Long branchId, Long productId) {
        return adjustmentHistory.stream()
                .filter(adj -> branchId == null || adj.getBranchId().equals(branchId))
                .filter(adj -> productId == null || adj.getProductId().equals(productId))
                .toList();
    }

    @Override
    public StockAdjustmentDTO createAdjustment(StockAdjustmentDTO adjustmentDTO) {
        if (!productRepository.existsById(adjustmentDTO.getProductId())) {
            throw new ResourceNotFoundException("Product not found with id: " + adjustmentDTO.getProductId());
        }

        Stock stock = stockRepository.findByBranchIdAndProductId(
                adjustmentDTO.getBranchId(),
                adjustmentDTO.getProductId()
        ).orElseGet(() -> {
            Stock newStock = new Stock();
            newStock.setBranchId(adjustmentDTO.getBranchId());
            newStock.setProductId(adjustmentDTO.getProductId());
            newStock.setQuantity(0);
            newStock.setReservedQty(0);
            return newStock;
        });

        int adjustmentQty = adjustmentDTO.getAdjustmentQty();

        switch (adjustmentDTO.getAdjustmentType()) {
            case "ADD":
            case "RETURN":
            case "CORRECTION":
                if (adjustmentQty > 0) {
                    stock.setQuantity(stock.getQuantity() + adjustmentQty);
                } else if (adjustmentQty < 0) {
                    if (stock.getQuantity() + adjustmentQty < 0) {
                        throw new InsufficientStockException("Cannot reduce stock below zero");
                    }
                    stock.setQuantity(stock.getQuantity() + adjustmentQty);
                }
                break;

            case "REMOVE":
            case "DAMAGE":
            case "LOSS":
                int removeQty = Math.abs(adjustmentQty);
                if (stock.getQuantity() < removeQty) {
                    throw new InsufficientStockException(
                        "Insufficient stock. Available: " + stock.getQuantity() + ", Required: " + removeQty
                    );
                }
                stock.setQuantity(stock.getQuantity() - removeQty);
                break;

            default:
                throw new IllegalArgumentException("Invalid adjustment type: " + adjustmentDTO.getAdjustmentType());
        }

        stockRepository.save(stock);
        adjustmentHistory.add(adjustmentDTO);

        return adjustmentDTO;
    }
}

