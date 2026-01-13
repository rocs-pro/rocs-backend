package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.SaleItem;
import java.util.List;

/**
 * PURPOSE: Interface for sale_items operations
 */
public interface SaleItemRepository {

    /**
     * Save a new sale item
     * @param saleItem - SaleItem entity
     * @return Generated sale_item_id
     */
    Long save(SaleItem saleItem);

    /**
     * Save multiple sale items (bulk insert for performance)
     * @param saleItemsses - List of sale items
     */
    void saveBatch(List<SaleItem> saleItemsses);

    /**
     * Get all items for a sale
     * @param saleId - Sale ID
     * @return List of sale items
     */
    List<SaleItem> findBySaleId(Long saleId);

    /**
     * Get all items for a sale with product details (JOIN query)
     * @param saleId - Sale ID
     * @return List of sale items with product names, SKU, etc.
     */
    List<SaleItem> findBySaleIdWithProductDetails(Long saleId);
}