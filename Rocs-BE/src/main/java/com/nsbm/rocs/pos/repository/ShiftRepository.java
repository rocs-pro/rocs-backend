package com.nsbm.rocs.pos.repository;

import com.nsbm.rocs.entity.pos.CashShifts;
import java.util.Optional;
import java.util.List;

/**
 * PURPOSE: Interface for all shift-related database operations
 * WHY INTERFACE? Follows Dependency Inversion Principle (SOLID)
 *
 * Contract: "Any class implementing this MUST provide these methods"
 */
public interface ShiftRepository {

    /**
     * Save a new shift to database
     * @param shift - CashShift entity to save
     * @return Generated shift_id
     */
    Long save(CashShifts shift);

    /**
     * Update existing shift (used when closing)
     * @param shift - CashShift entity with updated values
     */
    void update(CashShifts shift);

    /**
     * Find shift by ID
     * @param shiftId - Primary key
     * @return Optional<CashShifts> - Present if found, empty if not
     */
    Optional<CashShifts> findById(Long shiftId);

    /**
     * Find the currently open shift for a cashier
     * @param cashierId - User ID of cashier
     * @return Optional<CashShifts> - Present if found, empty if not
     */
    Optional<CashShifts> findOpenShiftByCashierId(Long cashierId);

    /**
     * Check if cashier has an open shift
     * @param cashierId - User ID of cashier
     * @return true if open shift exists, false otherwise
     */
    boolean hasOpenShift(Long cashierId);

    /**
     * Get shift with transaction statistics
     * Used for displaying shift summary with counts
     * @param shiftId - Primary key
     * @return Optional<CashShifts> with transaction stats
     */
    Optional<CashShifts> findByIdWithStats(Long shiftId);

    /**
     * Get all shifts for a specific branch
     * Used for reports
     * @param branchId - Branch ID
     * @param limit - Max number of records
     * @return List of shifts
     */
    List<CashShifts> findByBranchId(Long branchId, int limit);

    /**
     * Get all shifts for a specific cashier
     * Used for cashier history
     * @param cashierId - User ID
     * @param limit - Max number of records
     * @return List of shifts
     */
    List<CashShifts> findByCashierId(Long cashierId, int limit);
}