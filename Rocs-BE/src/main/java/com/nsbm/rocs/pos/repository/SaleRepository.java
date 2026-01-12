package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.Sales;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PURPOSE: Interface for all Sales-related database operations
 */
public interface SaleRepository {

    /**
     * Save a new Sales
     * @param Sales - Sales entity
     * @return Generated Sales_id
     */
    Long save(Sales Sales);

    /**
     * Find Sales by ID
     * @param SalesId - Primary key
     * @return Optional<Sales>
     */
    Optional<Sales> findById(Long SalesId);

    /**
     * Find Sales by invoice number
     * @param invoiceNo - Invoice number
     * @return Optional<Sales>
     */
    Optional<Sales> findByInvoiceNo(String invoiceNo);

    /**
     * Get all Saless for a shift
     * @param shiftId - Shift ID
     * @return List of Saless
     */
    List<Sales> findByShiftId(Long shiftId);

    /**
     * Get Saless by date range
     * @param branchId - Branch ID
     * @param startDate - Start date
     * @param endDate - End date
     * @return List of Saless
     */
    List<Sales> findByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get total Saless count for a shift
     * @param shiftId - Shift ID
     * @return Count
     */
    int countByShiftId(Long shiftId);
}