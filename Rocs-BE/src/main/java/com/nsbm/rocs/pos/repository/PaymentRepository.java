package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Payments;
import java.util.List;

/**
 * PURPOSE: Interface for payments operations
 */
public interface PaymentRepository {

    /**
     * Save a new payment
     * @param Payments - Payment entity
     * @return Generated payment_id
     */
    Long save(Payments Payments);

    /**
     * Save multiple payments (bulk insert)
     * @param payments - List of payments
     */
    void saveBatch(List<Payments> payments);

    /**
     * Get all payments for a sale
     * @param saleId - Sale ID
     * @return List of payments
     */
    List<Payments> findBySaleId(Long saleId);
}