package com.nsbm.rocs.inventory.repository;

import com.nsbm.rocs.entity.inventory.GRNItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GRNItemRepository extends JpaRepository<GRNItem, Long> {
    List<GRNItem> findByGrnId(Long grnId);
}

