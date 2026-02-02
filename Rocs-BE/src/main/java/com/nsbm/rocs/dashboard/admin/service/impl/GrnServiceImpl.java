package com.nsbm.rocs.dashboard.admin.service.impl;

import com.nsbm.rocs.dashboard.admin.dto.GrnDTO;
import com.nsbm.rocs.dashboard.admin.service.GrnService;
import com.nsbm.rocs.dashboard.manager.repository.GrnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin service for GRN related operations.
 */
@Service
public class GrnServiceImpl implements GrnService {

    private final GrnRepository grnRepository;

    @Autowired
    public GrnServiceImpl(GrnRepository grnRepository) {
        this.grnRepository = grnRepository;
    }

    /**
     * Return the count of pending GRNs for the given branch.
     * branchId is required; returns 0 when null.
     */
    @Override
    public Long getPendingGrnCount(Long branchId) {
        if (branchId == null) return 0L;
        return grnRepository.countByBranchIdAndStatus(branchId, "PENDING");
    }

    /**
     * Return the total count of pending GRNs across all branches.
     */
    @Override
    public Long getPendingGrnCountAll() {
        return grnRepository.countByStatus("PENDING");
    }

    /**
     * Return all pending GRNs across all branches mapped to GrnDTO.
     */
    @Override
    public List<GrnDTO> getAllPendingGrns() {
        List<Object[]> rows = grnRepository.findAllPendingGrnsWithDetails();
        List<GrnDTO> results = new ArrayList<>();
        if (rows == null) return results;

        for (Object[] r : rows) {
            if (r == null) continue;
            GrnDTO dto = new GrnDTO();

            // r[0] = g.grn_id
            if (r.length > 0 && r[0] != null) dto.setId(((Number) r[0]).longValue());

            // r[1] = g.grn_no
            if (r.length > 1 && r[1] != null) dto.setGrnNo(String.valueOf(r[1]));

            // r[2] = g.grn_date -> map to LocalDate
            if (r.length > 2 && r[2] != null) {
                Object dateObj = r[2];
                if (dateObj instanceof java.sql.Date) {
                    dto.setGrnDate(((java.sql.Date) dateObj).toLocalDate());
                } else if (dateObj instanceof Timestamp) {
                    dto.setGrnDate(((Timestamp) dateObj).toLocalDateTime().toLocalDate());
                } else {
                    try {
                        dto.setGrnDate(LocalDate.parse(dateObj.toString()));
                    } catch (Exception ignored) { /* leave null if parse fails */ }
                }
            }

            // r[3] = g.status
            if (r.length > 3 && r[3] != null) dto.setStatus(String.valueOf(r[3]));

            // r[4] = g.total_amount
            if (r.length > 4 && r[4] != null) {
                if (r[4] instanceof BigDecimal) dto.setTotalAmount((BigDecimal) r[4]);
                else if (r[4] instanceof Number) dto.setTotalAmount(BigDecimal.valueOf(((Number) r[4]).doubleValue()));
                else {
                    try { dto.setTotalAmount(new BigDecimal(String.valueOf(r[4]))); } catch (Exception ignored) {}
                }
            }

            // r[5] = supplier_name
            if (r.length > 5 && r[5] != null) dto.setSupplierName(String.valueOf(r[5]));

            // r[6] = item_count
            if (r.length > 6 && r[6] != null) {
                if (r[6] instanceof Number) dto.setItemCount(((Number) r[6]).intValue());
                else {
                    try { dto.setItemCount(Integer.parseInt(String.valueOf(r[6]))); } catch (Exception ignored) {}
                }
            }

            results.add(dto);
        }
        return results;
    }
}
