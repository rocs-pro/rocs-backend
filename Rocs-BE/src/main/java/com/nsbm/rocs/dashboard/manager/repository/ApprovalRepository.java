package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Approval entity operations
 * Provides methods for querying approval requests
 */
@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    /**
     * Find approvals by branch and status
     */
    List<Approval> findByBranchIdAndStatusOrderByCreatedAtDesc(Long branchId, String status);

    /**
     * Find all approvals for a branch
     */
    List<Approval> findByBranchIdOrderByCreatedAtDesc(Long branchId);

    /**
     * Count pending approvals for a branch
     */
    Long countByBranchIdAndStatus(Long branchId, String status);

    /**
     * Find approvals with user details
     */
    @Query(value = """
        SELECT a.approval_id, a.type, a.reference_no, a.status, a.created_at,
               u.full_name as requested_by_name, u.role as requested_by_role
        FROM approvals a
        JOIN user_profiles u ON a.requested_by = u.user_id
        WHERE a.branch_id = :branchId
        ORDER BY a.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findApprovalsWithUserDetails(@Param("branchId") Long branchId);

    /**
     * Find pending approvals with user details
     */
    @Query(value = """
        SELECT a.approval_id, a.type, a.reference_no, a.status, a.created_at,
               u.full_name as requested_by_name, u.role as requested_by_role
        FROM approvals a
        JOIN user_profiles u ON a.requested_by = u.user_id
        WHERE a.branch_id = :branchId AND a.status = :status
        ORDER BY a.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findApprovalsByStatusWithUserDetails(@Param("branchId") Long branchId, @Param("status") String status);
}

