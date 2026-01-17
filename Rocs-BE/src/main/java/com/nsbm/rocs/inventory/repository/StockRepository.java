package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.inventory.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByBranchIdAndProductId(Long branchId, Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InventoryStock s WHERE s.branchId = :branchId AND s.productId = :productId")
    Optional<Stock> findStockForUpdate(@Param("branchId") Long branchId, @Param("productId") Long productId);

    List<Stock> findByBranchId(Long branchId);

    List<Stock> findByProductId(Long productId);

    @Query("SELECT s FROM InventoryStock s WHERE s.branchId = :branchId AND s.availableQty < :threshold")
    List<Stock> findLowStockProducts(@Param("branchId") Long branchId, @Param("threshold") Integer threshold);

    @Query("SELECT s FROM InventoryStock s WHERE s.branchId = :branchId AND s.quantity > 0")
    List<Stock> findAvailableStock(@Param("branchId") Long branchId);

    @Modifying
    @Query("UPDATE InventoryStock s SET s.quantity = s.quantity + :qty WHERE s.branchId = :branchId AND s.productId = :productId")
    int incrementQuantity(@Param("branchId") Long branchId, @Param("productId") Long productId, @Param("qty") Integer qty);

    @Modifying
    @Query("UPDATE InventoryStock s SET s.quantity = s.quantity - :qty WHERE s.branchId = :branchId AND s.productId = :productId AND (s.quantity - s.reservedQty) >= :qty")
    int decrementQuantityIfAvailable(@Param("branchId") Long branchId, @Param("productId") Long productId, @Param("qty") Integer qty);

    @Modifying
    @Query("UPDATE InventoryStock s SET s.reservedQty = s.reservedQty + :qty WHERE s.branchId = :branchId AND s.productId = :productId AND (s.quantity - s.reservedQty) >= :qty")
    int incrementReservedIfAvailable(@Param("branchId") Long branchId, @Param("productId") Long productId, @Param("qty") Integer qty);

    @Modifying
    @Query("UPDATE InventoryStock s SET s.reservedQty = CASE WHEN (s.reservedQty - :qty) < 0 THEN 0 ELSE (s.reservedQty - :qty) END WHERE s.branchId = :branchId AND s.productId = :productId")
    int decrementReserved(@Param("branchId") Long branchId, @Param("productId") Long productId, @Param("qty") Integer qty);
}
