package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Payment;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PURPOSE: Interface for payments operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<@NonNull Payment, @NonNull Long>, PaymentRepositoryCustom {

    /**
     * Get all payments for a sale
     * @param saleId - Sale ID
     * @return List of payments
     */
    List<Payment> findBySaleId(Long saleId);

}