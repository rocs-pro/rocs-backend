package com.nsbm.rocs.entity.main;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a journal entry
 * Maps to the 'journal_entries' table in the database
 */
@Entity
@Table(name = "journal_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journal_id")
    private Long journalId;

    @Column(name = "journal_no", unique = true, nullable = false, length = 50)
    private String journalNo;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "transaction_type", length = 50)
    private String transactionType; // SALE, PURCHASE, PAYMENT, RECEIPT, ADJUSTMENT

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    @Column(name = "memo", length = 255)
    private String memo;

    @Column(name = "total_debit", precision = 15, scale = 2)
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Column(name = "total_credit", precision = 15, scale = 2)
    private BigDecimal totalCredit = BigDecimal.ZERO;

    @Column(name = "status", length = 20)
    private String status = "DRAFT"; // DRAFT, POSTED, VOID

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "posted_by")
    private Long postedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;
}

