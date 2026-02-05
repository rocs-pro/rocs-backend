package com.nsbm.rocs.manager.repository;

import com.nsbm.rocs.entity.pos.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ManagerSaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByBranchId(Long branchId);

    @Query("SELECT s FROM Sale s WHERE s.branchId = :branchId AND s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findByBranchIdAndDateRange(@Param("branchId") Long branchId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(s.netTotal), 0) FROM Sale s WHERE s.branchId = :branchId AND s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal sumNetTotalByBranchAndDateRange(@Param("branchId") Long branchId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(s.netTotal), 0) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal sumNetTotalByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.branchId = :branchId AND s.saleDate BETWEEN :startDate AND :endDate")
    Long countByBranchAndDateRange(@Param("branchId") Long branchId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    List<Sale> findTop10ByCustomerIdOrderBySaleDateDesc(Long customerId);
}

