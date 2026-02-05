package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.GRN;
import org.springframework.data.jpa.repository.JpaRepository;
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
}

