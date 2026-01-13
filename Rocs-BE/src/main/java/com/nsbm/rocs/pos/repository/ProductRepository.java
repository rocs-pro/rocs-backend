package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PURPOSE: Get product information during sales
 * Refactored to use Spring Data JPA - No implementation class needed.
 */
@Repository
public interface ProductRepository extends JpaRepository<@NonNull Product, @NonNull Long> {

    /**
     * Find product by ID (Must be active)
     */
    @Override
    @NonNull
    @Query("SELECT p FROM Product p WHERE p.productId = :productId AND p.isActive = true")
    Optional<Product> findById(@NonNull @Param("productId") Long productId);

    /**
     * Find product by SKU (Must be active)
     */
    @Query("SELECT p FROM Product p WHERE p.sku = :sku AND p.isActive = true")
    Optional<Product> findBySku(@Param("sku") String sku);

    /**
     * Find product by Barcode (Must be active)
     */
    @Query("SELECT p FROM Product p WHERE p.barcode = :barcode AND p.isActive = true")
    Optional<Product> findByBarcode(@Param("barcode") String barcode);

}