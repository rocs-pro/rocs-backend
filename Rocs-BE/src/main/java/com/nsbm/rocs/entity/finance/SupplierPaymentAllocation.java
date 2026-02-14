package com.nsbm.rocs.entity.finance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "supplier_payment_allocations")
@Getter
@Setter
@NoArgsConstructor
public class SupplierPaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "grn_id", nullable = false)
    private Long grnId;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
}

