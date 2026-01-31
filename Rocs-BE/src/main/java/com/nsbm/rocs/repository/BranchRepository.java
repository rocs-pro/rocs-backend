package com.nsbm.rocs.repository;

import com.nsbm.rocs.entity.main.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Branch entity operations
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCode(String code);

    boolean existsByCode(String code);
}

