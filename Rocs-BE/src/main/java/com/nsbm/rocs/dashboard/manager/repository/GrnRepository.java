package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.Grn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for GRN entity operations
 * Provides methods for querying pending GRNs for manager dashboard
 */
@Repository
public interface GrnRepository extends JpaRepository<Grn, Long> {

    /**
     * Find pending GRNs for a branch
     */
    List<Grn> findByBranchIdAndStatusOrderByCreatedAtDesc(Long branchId, String status);

    /**
     * Count pending GRNs for a branch
     */
    Long countByBranchIdAndStatus(Long branchId, String status);

    /**
     * Find pending GRNs with supplier name
     */
    @Query(value = """
        SELECT g.grn_id, g.grn_no, g.grn_date, g.status, g.total_amount,
               s.name as supplier_name,
               (SELECT COUNT(*) FROM grn_items gi WHERE gi.grn_id = g.grn_id) as item_count
        FROM grns g
        JOIN suppliers s ON g.supplier_id = s.supplier_id
        WHERE g.branch_id = :branchId AND g.status = 'PENDING'
        ORDER BY g.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findPendingGrnsWithDetails(@Param("branchId") Long branchId);
}

