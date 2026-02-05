package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Payment;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Delete payments by sale ID
     * @param saleId - Sale ID
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteBySaleId(Long saleId);

    /**
     * Get payments by shift ID (via Sale)
     * @param shiftId - Shift ID
     * @return List of payments
     */
    @Query("SELECT p FROM Payment p JOIN Sale s ON p.saleId = s.saleId WHERE s.shiftId = :shiftId")
    List<Payment> findByShiftId(Long shiftId);

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN Sale s ON p.saleId = s.saleId WHERE s.shiftId = :shiftId AND p.paymentType = :paymentType")
    java.math.BigDecimal sumTotalByShiftIdAndType(Long shiftId, String paymentType);

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN Sale s ON p.saleId = s.saleId WHERE s.shiftId = :shiftId AND p.paymentType NOT IN ('CASH', 'CARD')")
    java.math.BigDecimal sumOtherPaymentsByShiftId(Long shiftId);
}