package com.nsbm.rocs.auth.repo;

import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.enums.Role;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@NullMarked
public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByEmail(String email);
    Optional<UserProfile> findByPhone(String phone);
    Optional<UserProfile> findByEmployeeId(String employeeId);

    @Query("SELECT u FROM UserProfile u WHERE u.branch.branchId = :branchId AND u.role = :role")
    List<UserProfile> findByBranch_BranchIdAndRole(@Param("branchId") Long branchId, @Param("role") Role role);
}
