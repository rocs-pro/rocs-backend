package com.nsbm.rocs.inventory.service.impl;

import com.nsbm.rocs.inventory.dto.GRNCreateRequestDTO;
import com.nsbm.rocs.inventory.dto.GRNResponseDTO;
import com.nsbm.rocs.inventory.service.GRNService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Simple validation class to test GRN functionality
 * This is NOT a JUnit test, but a utility to verify the service works
 */
@Component
@Slf4j
public class GRNServiceValidator {

    @Autowired
    private GRNService grnService;

    /**
     * Method to validate basic GRN service functionality
     * Call this manually to test the service
     */
    public void validateGRNService() {
        try {
            log.info("=== Starting GRN Service Validation ===");

            // Test 1: Check if GRN number generation works
            boolean exists = grnService.isGRNNumberExists("GRN-1-20240205-001");
            log.info("GRN number check result: {}", exists);

            // Test 2: Try to get GRNs by branch (should return empty list initially)
            List<GRNResponseDTO> grns = grnService.getGRNsByBranch(1L);
            log.info("Found {} GRNs for branch 1", grns.size());

            // Test 3: Create a sample GRN request (won't actually create due to missing data)
            GRNCreateRequestDTO.GRNItemCreateDTO item = new GRNCreateRequestDTO.GRNItemCreateDTO();
            item.setProductId(1L);
            item.setQtyReceived(new BigDecimal("10.000"));
            item.setUnitPrice(new BigDecimal("100.00"));
            item.setBatchCode("BATCH001");

            GRNCreateRequestDTO request = new GRNCreateRequestDTO();
            request.setBranchId(1L);
            request.setSupplierId(1L);
            request.setGrnDate(LocalDate.now());
            request.setItems(Arrays.asList(item));

            log.info("Sample GRN request created with {} items", request.getItems().size());

            log.info("=== GRN Service Validation Completed Successfully ===");

        } catch (Exception e) {
            log.error("GRN Service validation failed: {}", e.getMessage(), e);
        }
    }
}
