package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.StockAdjustmentDTO;

import java.util.List;

public interface AdjustmentService {

    List<StockAdjustmentDTO> getAllAdjustments(Long branchId, Long productId);

    StockAdjustmentDTO createAdjustment(StockAdjustmentDTO adjustmentDTO);
}

