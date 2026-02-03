package com.nsbm.rocs.dashboard.admin.repository;

import com.nsbm.rocs.entity.main.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("dashboardAdminBranchRepository")
public interface BranchRepository extends JpaRepository< Branch, Long> {
}
