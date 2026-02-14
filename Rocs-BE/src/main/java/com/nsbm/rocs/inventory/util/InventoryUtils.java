package com.nsbm.rocs.inventory.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InventoryUtils {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generate a unique code with prefix, branch, date, and sequence
     */
    public static String generateCode(String prefix, Long branchId, Long sequence) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        return String.format("%s-%d-%s-%03d", prefix, branchId, dateStr, sequence);
    }

    /**
     * Validate GRN status for updates
     */
    public static boolean canModifyGRN(String status) {
        return "PENDING".equals(status);
    }

    /**
     * Validate GRN status for approval
     */
    public static boolean canApproveGRN(String status) {
        return "PENDING".equals(status);
    }

    /**
     * Validate payment status update
     */
    public static boolean canUpdatePaymentStatus(String grnStatus) {
        return "APPROVED".equals(grnStatus);
    }

    /**
     * Check if payment status is valid
     */
    public static boolean isValidPaymentStatus(String paymentStatus) {
        return paymentStatus != null &&
               (paymentStatus.equals("PAID") ||
                paymentStatus.equals("UNPAID") ||
                paymentStatus.equals("PARTIAL"));
    }
}
