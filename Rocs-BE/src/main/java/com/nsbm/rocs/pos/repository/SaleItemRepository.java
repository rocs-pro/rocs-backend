package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.SaleItems;
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
    Long save(SaleItems saleItem);

    /**
     * Save multiple sale items (bulk insert for performance)
     * @param SaleItemss - List of sale items
     */
    void saveBatch(List<SaleItems> SaleItemss);

    /**
     * Get all items for a sale
     * @param saleId - Sale ID
     * @return List of sale items
     */
    List<SaleItems> findBySaleId(Long saleId);

    /**
     * Get all items for a sale with product details (JOIN query)
     * @param saleId - Sale ID
     * @return List of sale items with product names, SKU, etc.
     */
    List<SaleItems> findBySaleIdWithProductDetails(Long saleId);
}