package com.nsbm.rocs.entity.pos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_flows")
@Getter
@Setter
@NoArgsConstructor
public class CashFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flow_id")
    private Long id;

    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "type", nullable = false)
    private String type; // PAID_IN, PAID_OUT

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private String reason;

    @Column(name = "reference_no", length = 50)
    private String referenceNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
