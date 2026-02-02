package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.SalesReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesReturnRepository extends JpaRepository<SalesReturn, Long> {
    Optional<SalesReturn> findByReturnNo(String returnNo);
    List<SalesReturn> findByBranchId(Long branchId);
    List<SalesReturn> findBySaleId(Long saleId);
    List<SalesReturn> findByStatus(String status);
}

