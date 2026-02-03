package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.inventory.GRN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrnRepository extends JpaRepository<GRN, Long> {

    Long countByBranchIdAndStatus(Long branchId, String status);

    Long countByStatus(String status);

    @Query(value = "SELECT g.grn_id, g.grn_no, g.grn_date, g.status, g.total_amount, s.name as supplier_name, COUNT(gi.grn_item_id) as item_count " +
            "FROM grns g " +
            "LEFT JOIN suppliers s ON g.supplier_id = s.supplier_id " +
            "LEFT JOIN grn_items gi ON g.grn_id = gi.grn_id " +
            "WHERE g.status = 'PENDING' " +
            "GROUP BY g.grn_id, g.grn_no, g.grn_date, g.status, g.total_amount, s.name",
            nativeQuery = true)
    List<Object[]> findAllPendingGrnsWithDetails();
}

