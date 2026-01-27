package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {
    List<CashFlow> findByShiftId(Long shiftId);
}

