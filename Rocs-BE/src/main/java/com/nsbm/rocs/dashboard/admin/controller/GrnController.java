package com.nsbm.rocs.dashboard.admin.controller;

import com.nsbm.rocs.dashboard.admin.dto.GrnDTO;
import com.nsbm.rocs.dashboard.admin.service.GrnService;
import com.nsbm.rocs.dashboard.admin.service.impl.GrnServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/grns")
public class GrnController {

    private final GrnService grnService;

    @Autowired
    public GrnController(GrnServiceImpl grnService) {
        this.grnService = grnService;
    }

    /**
     * GET /api/admin/grns/pending
     * Returns all pending GRNs across all branches.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<GrnDTO>> getAllPendingGrns() {
        List<GrnDTO> list = grnService.getAllPendingGrns();
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/admin/grns/pending/count?branchId={branchId}
     * If branchId is provided returns count for that branch, otherwise returns total pending count.
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Long> getPendingGrnCount(@RequestParam(value = "branchId", required = false) Long branchId) {
        Long count = (branchId == null) ? grnService.getPendingGrnCountAll() : grnService.getPendingGrnCount(branchId);
        return ResponseEntity.ok(count);
    }
}
