package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Account entity operations
 * Provides methods for querying chart of accounts
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all active accounts
     */
    List<Account> findByIsActiveTrueOrderByCodeAsc();

    /**
     * Find accounts by type
     */
    List<Account> findByTypeAndIsActiveTrueOrderByCodeAsc(String type);

    /**
     * Find by account code
     */
    Account findByCode(String code);
}

