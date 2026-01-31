package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByProductId(Long productId);

    List<Batch> findByBranchId(Long branchId);

    List<Batch> findByProductIdAndBranchId(Long productId, Long branchId);

    // Deprecated single lookup; keep for compatibility but unused
    Optional<Batch> findByBatchCode(String batchCode);

    // Return all matches for duplicate batch codes
    @Query("SELECT b FROM InventoryBatch b WHERE b.branchId = :branchId AND b.productId = :productId AND b.batchCode = :batchCode ORDER BY (CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END) ASC, b.expiryDate ASC, b.manufacturingDate ASC, b.batchId ASC")
    List<Batch> findAllByBranchIdAndProductIdAndBatchCodeOrdered(@Param("branchId") Long branchId,
                                                                  @Param("productId") Long productId,
                                                                  @Param("batchCode") String batchCode);

    @Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate < :currentDate AND b.qty > 0")
    List<Batch> findExpiredBatches(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate BETWEEN :startDate AND :endDate AND b.qty > 0")
    List<Batch> findExpiringSoonBatches(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM InventoryBatch b WHERE b.branchId = :branchId AND b.qty > 0 ORDER BY (CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END) ASC, b.expiryDate ASC, b.manufacturingDate ASC, b.batchId ASC")
    List<Batch> findAvailableBatchesByExpiryDate(@Param("branchId") Long branchId);

    @Query("SELECT b FROM InventoryBatch b WHERE b.branchId = :branchId AND b.productId = :productId AND b.qty > 0 AND (b.expiryDate IS NULL OR b.expiryDate >= :currentDate) ORDER BY (CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END) ASC, b.expiryDate ASC, b.manufacturingDate ASC, b.batchId ASC")
    List<Batch> findAvailableByBranchAndProductFefo(@Param("branchId") Long branchId,
                                                    @Param("productId") Long productId,
                                                    @Param("currentDate") java.time.LocalDate currentDate);

    @Query("SELECT b FROM InventoryBatch b WHERE b.branchId = :branchId AND b.productId = :productId AND b.qty > 0 AND b.expiryDate < :currentDate")
    List<Batch> findExpiredByBranchAndProduct(@Param("branchId") Long branchId,
                                              @Param("productId") Long productId,
                                              @Param("currentDate") java.time.LocalDate currentDate);

    @Query("SELECT b FROM InventoryBatch b WHERE b.branchId = :branchId AND b.productId = :productId AND b.qty > 0 AND b.expiryDate BETWEEN :startDate AND :endDate")
    List<Batch> findExpiringSoonByBranchAndProduct(@Param("branchId") Long branchId,
                                                   @Param("productId") Long productId,
                                                   @Param("startDate") java.time.LocalDate startDate,
                                                   @Param("endDate") java.time.LocalDate endDate);
}

