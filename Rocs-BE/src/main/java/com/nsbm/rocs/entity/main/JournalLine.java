package com.nsbm.rocs.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity representing a journal entry line
 * Maps to the 'journal_lines' table in the database
 */
@Entity
@Table(name = "journal_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    @Column(name = "journal_id", nullable = false)
    private Long journalId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "debit", precision = 12, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO;

    @Column(name = "credit", precision = 12, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    @Column(name = "description", length = 255)
    private String description;
}

