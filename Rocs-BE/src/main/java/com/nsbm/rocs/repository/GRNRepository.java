package com.nsbm.rocs.repository;

import com.nsbm.rocs.entity.inventory.GRN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


    // ADMIN QUERIES
    /**
     * Count pending GRNs for a branch
     */
    Long countByBranchIdAndStatus(Long branchId, String status);

    /**
     * Find all pending GRNs with supplier name (no branch filter)
     */
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

    Long countByStatus(String pending);
}

