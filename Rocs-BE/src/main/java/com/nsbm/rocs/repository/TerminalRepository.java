package com.nsbm.rocs.repository;

import com.nsbm.rocs.entity.main.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {
    Optional<Terminal> findByTerminalCode(String terminalCode);
    List<Terminal> findByBranchId(Long branchId);
    List<Terminal> findByBranchIdAndIsActiveTrue(Long branchId);
}
