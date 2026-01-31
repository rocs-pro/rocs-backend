package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.UserActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for UserActivityLog entity operations
 * Provides methods for querying activity logs
 */
@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    /**
     * Find activity logs by branch
     */
    List<UserActivityLog> findByBranchIdOrderByCreatedAtDesc(Long branchId, Pageable pageable);

    /**
     * Find activity logs with user names
     */
    @Query(value = """
        SELECT ual.activity_id, ual.activity_type, ual.description, ual.created_at,
               u.full_name as user_name, u.role
        FROM user_activity_log ual
        LEFT JOIN user_profiles u ON ual.user_id = u.user_id
        WHERE ual.branch_id = :branchId
        ORDER BY ual.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findActivityLogsWithUserNames(@Param("branchId") Long branchId, @Param("limit") int limit);
}

