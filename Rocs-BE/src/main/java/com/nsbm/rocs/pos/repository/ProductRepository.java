package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Product;
import java.util.Optional;

/**
 * PURPOSE: Get product information during sales
 */
public interface ProductRepository {

    /**
     * Find product by ID
     */
    Optional<Product> findById(Long productId);

    /**
     * Find product by SKU
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find product by barcode
     */
    Optional<Product> findByBarcode(String barcode);
}