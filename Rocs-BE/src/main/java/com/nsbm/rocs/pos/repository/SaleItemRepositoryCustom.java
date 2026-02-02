package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.SaleItem;
import com.nsbm.rocs.pos.dto.sale.SaleItemResponse;

import java.util.List;

public interface SaleItemRepositoryCustom {
    void saveBatch(List<SaleItem> saleItems);
    List<SaleItemResponse> findBySaleIdWithProductDetails(Long saleId);
}
