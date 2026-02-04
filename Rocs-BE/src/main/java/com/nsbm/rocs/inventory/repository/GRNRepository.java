package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.GRN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    List<GRN> findByBranchIdAndStatus(Long branchId, String status);
    List<GRN> findByBranchIdAndSupplierId(Long branchId, Long supplierId);
    List<GRN> findByInvoiceNoContainingIgnoreCase(String invoiceNo);
    List<GRN> findByCreatedBy(Long createdBy);

    @Query("SELECT g FROM GRN g WHERE " +
           "(:branchId IS NULL OR g.branchId = :branchId) AND " +
           "(:supplierId IS NULL OR g.supplierId = :supplierId) AND " +
           "(:status IS NULL OR g.status = :status) AND " +
           "(:paymentStatus IS NULL OR g.paymentStatus = :paymentStatus) AND " +
           "(:startDate IS NULL OR g.grnDate >= :startDate) AND " +
           "(:endDate IS NULL OR g.grnDate <= :endDate) AND " +
           "(:grnNo IS NULL OR LOWER(g.grnNo) LIKE LOWER(CONCAT('%', :grnNo, '%'))) AND " +
           "(:invoiceNo IS NULL OR LOWER(g.invoiceNo) LIKE LOWER(CONCAT('%', :invoiceNo, '%')))")
    List<GRN> findByFilters(@Param("branchId") Long branchId,
                           @Param("supplierId") Long supplierId,
                           @Param("status") String status,
                           @Param("paymentStatus") String paymentStatus,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate,
                           @Param("grnNo") String grnNo,
                           @Param("invoiceNo") String invoiceNo);

    @Query("SELECT COUNT(g) FROM GRN g WHERE g.branchId = :branchId AND g.grnDate = :date")
    Long countByBranchIdAndGrnDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);
}

