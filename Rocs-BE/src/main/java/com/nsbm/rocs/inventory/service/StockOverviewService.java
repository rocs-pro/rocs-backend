package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.StockDTO;

import java.util.List;

public interface StockOverviewService {

    List<StockDTO> getStockOverview(Long branchId);

    List<StockDTO> getLowStockProducts(Long branchId, Integer threshold);
}

