package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.JournalEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for JournalEntry entity operations
 * Provides methods for querying journal entries
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    /**
     * Find journal entries by branch ordered by date descending
     */
    List<JournalEntry> findByBranchIdOrderByEntryDateDesc(Long branchId, Pageable pageable);

    /**
     * Find all journal entries by branch
     */
    List<JournalEntry> findByBranchIdOrderByEntryDateDesc(Long branchId);

    /**
     * Generate next journal number
     */
    @Query(value = "SELECT CONCAT('JE-', LPAD(COALESCE(MAX(CAST(SUBSTRING(journal_no, 4) AS UNSIGNED)), 0) + 1, 4, '0')) FROM journal_entries", nativeQuery = true)
    String generateNextJournalNo();
}

