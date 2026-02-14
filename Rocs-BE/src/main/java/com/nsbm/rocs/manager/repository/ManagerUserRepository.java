package com.nsbm.rocs.manager.repository;

import com.nsbm.rocs.entity.main.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerUserRepository extends JpaRepository<UserProfile, Long> {

    List<UserProfile> findByBranch_BranchId(Long branchId);

    @Query("SELECT u FROM UserProfile u WHERE u.branch.branchId = :branchId AND u.accountStatus = 'ACTIVE'")
    List<UserProfile> findActiveByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT u FROM UserProfile u WHERE u.accountStatus = 'ACTIVE'")
    List<UserProfile> findAllActive();
}

