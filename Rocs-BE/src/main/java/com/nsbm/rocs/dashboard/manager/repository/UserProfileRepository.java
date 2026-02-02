package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserProfile entity operations
 * Provides methods for querying staff data
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Find users by branch
     */
    @Query("SELECT u FROM UserProfile u WHERE u.branch.id = :branchId ORDER BY u.lastLogin DESC")
    List<UserProfile> findByBranchId(@Param("branchId") Long branchId);

    /**
     * Find by username
     */
    Optional<UserProfile> findByUsername(String username);

    /**
     * Find active users by branch
     */
    @Query("SELECT u FROM UserProfile u WHERE u.branch.id = :branchId AND u.accountStatus = 'ACTIVE' ORDER BY u.lastLogin DESC")
    List<UserProfile> findActiveUsersByBranchId(@Param("branchId") Long branchId);

    /**
     * Count active users by branch
     */
    @Query("SELECT COUNT(u) FROM UserProfile u WHERE u.branch.id = :branchId AND u.accountStatus = 'ACTIVE'")
    Long countActiveUsersByBranchId(@Param("branchId") Long branchId);
}

