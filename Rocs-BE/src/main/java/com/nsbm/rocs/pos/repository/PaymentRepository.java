package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Payment;
import java.util.List;

/**
 * PURPOSE: Interface for payments operations
 */
public interface PaymentRepository {

    /**
     * Save a new payment
     * @param Payment - Payment entity
     * @return Generated payment_id
     */
    Long save(Payment Payment);

    /**
     * Save multiple payments (bulk insert)
     * @param payments - List of payments
     */
    void saveBatch(List<Payment> payments);

    /**
     * Get all payments for a sale
     * @param saleId - Sale ID
     * @return List of payments
     */
    List<Payment> findBySaleId(Long saleId);
}