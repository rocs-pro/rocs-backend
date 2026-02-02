package com.nsbm.rocs.config;

import com.nsbm.rocs.auth.repo.BranchRepo;
import com.nsbm.rocs.dashboard.manager.repository.ApprovalRepository;
import com.nsbm.rocs.entity.main.Approval;
import com.nsbm.rocs.entity.main.Branch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initialize database with sample data for testing
 * This will run on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ApprovalRepository approvalRepository;
    private final BranchRepo branchRepo;

    @Override
    public void run(String... args) {
        log.info("Initializing database with sample data...");

        try {
            // Create default branch if it doesn't exist
            if (branchRepo.count() == 0) {
                Branch branch = new Branch();
                branch.setName("Main Branch");
                branch.setCode("BR001");
                branch.setAddress("123 Main Street, Colombo");
                branch.setLocation("Colombo");
                branch.setPhone("0112345678");
                branch.setEmail("main@rocs.lk");
                branch.setIsActive(true);
                branchRepo.save(branch);
                log.info("Created default branch: Main Branch (ID: 1)");
            } else {
                log.info("Branch already exists. Count: {}", branchRepo.count());
            }

            // Create sample approvals if none exist
            if (approvalRepository.count() == 0) {
                createSampleApproval(1L, "GRN", "GRN-1021", 1L, "PENDING");
                createSampleApproval(1L, "PRICE_CHANGE", "INV-88912", 2L, "PENDING");
                createSampleApproval(1L, "RETURN", "RET-771", 3L, "PENDING");
                createSampleApproval(1L, "DISCOUNT", "DISC-445", 1L, "APPROVED");
                createSampleApproval(1L, "STOCK_ADJUST", "ADJ-332", 2L, "REJECTED");

                log.info("Created {} sample approvals", approvalRepository.count());
            } else {
                log.info("Sample approvals already exist. Count: {}", approvalRepository.count());
            }

            log.info("Database initialization completed successfully");

        } catch (Exception e) {
            log.error("Error initializing database: {}", e.getMessage(), e);
        }
    }

    private void createSampleApproval(Long branchId, String type, String referenceNo, Long requestedBy, String status) {
        Approval approval = new Approval();
        approval.setBranchId(branchId);
        approval.setType(type);
        approval.setReferenceNo(referenceNo);
        approval.setRequestedBy(requestedBy);
        approval.setStatus(status);
        approval.setRequestNotes("Sample request for testing");

        if (!"PENDING".equals(status)) {
            approval.setApprovedBy(1L);
            approval.setApprovedAt(LocalDateTime.now().minusDays(1));
            approval.setApprovalNotes("Processed by system");
        }

        approvalRepository.save(approval);
    }
}

