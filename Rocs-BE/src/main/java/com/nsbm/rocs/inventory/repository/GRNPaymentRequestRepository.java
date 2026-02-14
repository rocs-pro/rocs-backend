package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.GRNPaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GRNPaymentRequestRepository extends JpaRepository<GRNPaymentRequest, Long> {

    List<GRNPaymentRequest> findByBranchId(Long branchId);

    List<GRNPaymentRequest> findByStatus(String status);

    List<GRNPaymentRequest> findByBranchIdAndStatus(Long branchId, String status);

    Optional<GRNPaymentRequest> findByGrnId(Long grnId);

    @Query("SELECT r FROM GRNPaymentRequest r WHERE r.branchId = :branchId AND r.status IN :statuses ORDER BY r.createdAt DESC")
    List<GRNPaymentRequest> findByBranchIdAndStatusIn(@Param("branchId") Long branchId, @Param("statuses") List<String> statuses);

    @Query("SELECT r FROM GRNPaymentRequest r WHERE r.status IN ('PENDING', 'SUPERVISOR_APPROVED') AND r.branchId = :branchId ORDER BY r.priority DESC, r.createdAt ASC")
    List<GRNPaymentRequest> findPendingRequestsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT r FROM GRNPaymentRequest r WHERE r.status IN ('TRANSFERRED_TO_MANAGER', 'PROCESSING') ORDER BY r.priority DESC, r.transferredAt ASC")
    List<GRNPaymentRequest> findManagerPendingRequests();

    @Query("SELECT COUNT(r) FROM GRNPaymentRequest r WHERE r.branchId = :branchId AND r.status IN ('PENDING', 'SUPERVISOR_APPROVED')")
    Long countPendingByBranch(@Param("branchId") Long branchId);
    
    @Query("SELECT COUNT(r) FROM GRNPaymentRequest r WHERE r.status IN ('TRANSFERRED_TO_MANAGER', 'PROCESSING')")
    Long countManagerPending();
}
