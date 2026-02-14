package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.GRNItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GRNItemRepository extends JpaRepository<GRNItem, Long> {
    List<GRNItem> findByGrnId(Long grnId);
    List<GRNItem> findByProductId(Long productId);
    List<GRNItem> findByBatchCode(String batchCode);
    List<GRNItem> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(gi.qtyReceived) FROM GRNItem gi WHERE gi.productId = :productId")
    BigDecimal getTotalReceivedQuantityByProduct(@Param("productId") Long productId);

    @Query("SELECT gi FROM GRNItem gi JOIN GRN g ON gi.grnId = g.grnId WHERE g.branchId = :branchId AND gi.productId = :productId")
    List<GRNItem> findByBranchIdAndProductId(@Param("branchId") Long branchId, @Param("productId") Long productId);
}

