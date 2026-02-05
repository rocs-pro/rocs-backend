package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.SaleItem;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PURPOSE: Interface for sale_items operations
 */
@Repository
public interface SaleItemRepository extends JpaRepository<@NonNull SaleItem, @NonNull Long>, SaleItemRepositoryCustom {

    /**
     * Get all items for a sale
     * @param saleId - Sale ID
     * @return List of sale items
     */
    List<SaleItem> findBySaleId(Long saleId);

    /**
     * Delete items by sale ID
     * @param saleId - Sale ID
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteBySaleId(Long saleId);

}