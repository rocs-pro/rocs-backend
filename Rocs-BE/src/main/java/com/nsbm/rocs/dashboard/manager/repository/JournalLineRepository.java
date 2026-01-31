package com.nsbm.rocs.dashboard.manager.repository;

import com.nsbm.rocs.entity.main.JournalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for JournalLine entity operations
 * Provides methods for querying journal entry lines
 */
@Repository
public interface JournalLineRepository extends JpaRepository<JournalLine, Long> {

    /**
     * Find lines by journal entry ID
     */
    List<JournalLine> findByJournalIdOrderByLineIdAsc(Long journalId);

    /**
     * Find lines with account names
     */
    @Query(value = """
        SELECT jl.line_id, jl.debit, jl.credit, jl.description, a.name as account_name, a.code as account_code
        FROM journal_lines jl
        JOIN accounts a ON jl.account_id = a.account_id
        WHERE jl.journal_id = :journalId
        ORDER BY jl.line_id
        """, nativeQuery = true)
    List<Object[]> findLinesWithAccountNames(@Param("journalId") Long journalId);
}

