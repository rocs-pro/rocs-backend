package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.GRN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GRNRepository extends JpaRepository<GRN, Long> {
    Optional<GRN> findByGrnNo(String grnNo);
    List<GRN> findByBranchId(Long branchId);
    List<GRN> findBySupplierId(Long supplierId);
    List<GRN> findByPoId(Long poId);
    List<GRN> findByStatus(String status);
    List<GRN> findByPaymentStatus(String paymentStatus);
    List<GRN> findByBranchIdAndGrnDateBetween(Long branchId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT g FROM GRN g WHERE " +
            "(:branchId IS NULL OR g.branchId = :branchId) AND " +
            "(:supplierId IS NULL OR g.supplierId = :supplierId) AND " +
            "(:status IS NULL OR g.status = :status) AND " +
            "(:paymentStatus IS NULL OR g.paymentStatus = :paymentStatus) AND " +
            "(:startDate IS NULL OR g.grnDate >= :startDate) AND " +
            "(:endDate IS NULL OR g.grnDate <= :endDate) AND " +
            "(:grnNo IS NULL OR lower(g.grnNo) LIKE lower(concat('%', :grnNo, '%'))) AND " +
            "(:invoiceNo IS NULL OR lower(g.invoiceNo) LIKE lower(concat('%', :invoiceNo, '%')))")
    List<GRN> findByFilters(
            @Param("branchId") Long branchId,
            @Param("supplierId") Long supplierId,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("grnNo") String grnNo,
            @Param("invoiceNo") String invoiceNo
    );

    long countByBranchIdAndGrnDate(Long branchId, LocalDate grnDate);

    // ADMIN QUERIES
    Long countByBranchIdAndStatus(Long branchId, String status);

    @Query(value = """
            SELECT g.grn_id, g.grn_no, g.grn_date, g.status, g.total_amount,
               s.name as supplier_name,
               (SELECT COUNT(*) FROM grn_items gi WHERE gi.grn_id = g.grn_id) as item_count
            FROM grns g
            JOIN suppliers s ON g.supplier_id = s.supplier_id
            WHERE g.status = 'PENDING'
            ORDER BY g.created_at DESC
            """, nativeQuery = true)
    List<Object[]> findAllPendingGrnsWithDetails();

    Long countByStatus(String status);
}

