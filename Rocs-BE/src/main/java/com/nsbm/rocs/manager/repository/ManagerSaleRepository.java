package com.nsbm.rocs.manager.repository;

import com.nsbm.rocs.entity.pos.Sale;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC")
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
    
    // Recent sales with limit
    @Query("SELECT s FROM Sale s ORDER BY s.saleDate DESC")
    List<Sale> findRecentSales(Pageable pageable);
    
    // Hourly sales data
    @Query(value = """
        SELECT HOUR(s.sale_date) as hour_of_day, 
               COALESCE(SUM(s.net_total), 0) as total_sales,
               COUNT(s.sale_id) as transaction_count
        FROM sales s 
        WHERE DATE(s.sale_date) = DATE(:targetDate)
        GROUP BY HOUR(s.sale_date)
        ORDER BY hour_of_day
        """, nativeQuery = true)
    List<Object[]> findHourlySales(@Param("targetDate") LocalDateTime targetDate);
    
    // Count distinct customers served
    @Query("SELECT COUNT(DISTINCT s.customerId) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate AND s.customerId IS NOT NULL")
    Long countDistinctCustomers(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
    
    // Get gross total sum (before discounts)
    @Query("SELECT COALESCE(SUM(s.grossTotal), 0) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal sumGrossTotalByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    // Get discounts total
    @Query("SELECT COALESCE(SUM(s.discount), 0) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal sumDiscountsByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Get sales revenue and count by terminal
    @Query("SELECT cs.terminalId, COUNT(s), COALESCE(SUM(s.netTotal), 0) " +
           "FROM Sale s JOIN com.nsbm.rocs.entity.pos.CashShift cs ON s.shiftId = cs.shiftId " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY cs.terminalId")
    List<Object[]> findRevenueByTerminal(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Get payment breakdown by terminal
    @Query("SELECT cs.terminalId, p.paymentType, COALESCE(SUM(p.amount), 0) " +
           "FROM com.nsbm.rocs.entity.pos.Payment p " +
           "JOIN com.nsbm.rocs.entity.pos.Sale s ON p.saleId = s.saleId " +
           "JOIN com.nsbm.rocs.entity.pos.CashShift cs ON s.shiftId = cs.shiftId " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY cs.terminalId, p.paymentType")
    List<Object[]> findPaymentsByTerminal(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}
