package com.nsbm.rocs.pos.service;

import com.nsbm.rocs.auth.repo.UserProfileRepo;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.pos.CashShift;
import com.nsbm.rocs.pos.dto.ShiftStartRequest;
import com.nsbm.rocs.pos.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final AuthenticationManager authenticationManager;
    private final UserProfileRepo userProfileRepo;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository, AuthenticationManager authenticationManager, UserProfileRepo userProfileRepo) {
        this.shiftRepository = shiftRepository;
        this.authenticationManager = authenticationManager;
        this.userProfileRepo = userProfileRepo;
    }

    @Transactional
    public Long startShift(ShiftStartRequest request) {
        Long supervisorId = null;
        LocalDateTime approvedAt = null;

        // 1. Validate Supervisor Credentials (if required)
        if (request.getSupervisor() != null &&
            request.getSupervisor().getUsername() != null &&
            !request.getSupervisor().getUsername().isEmpty()) {

            try {
                Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getSupervisor().getUsername(),
                        request.getSupervisor().getPassword()
                    )
                );

                if (!auth.isAuthenticated()) {
                    throw new RuntimeException("Invalid supervisor credentials");
                }

                // Get supervisor ID
                UserProfile supervisor = userProfileRepo.findByUsername(request.getSupervisor().getUsername())
                        .orElseThrow(() -> new RuntimeException("Supervisor user not found"));

                // Optional: Check if user has supervisor role
                // if (!supervisor.getRole().getName().equals("SUPERVISOR")) ...

                supervisorId = supervisor.getUserId();
                approvedAt = LocalDateTime.now();

            } catch (Exception e) {
                throw new RuntimeException("Supervisor authentication failed: " + e.getMessage());
            }
        }

        // 2. Create CashShift Entity
        CashShift shift = new CashShift();
        shift.setShiftNo(generateShiftNo(request.getBranchId()));
        shift.setBranchId(request.getBranchId());
        shift.setTerminalId(request.getTerminalId());
        shift.setCashierId(request.getCashierId());
        shift.setOpeningCash(request.getOpeningCash());
        shift.setOpenedAt(LocalDateTime.now());
        shift.setStatus("OPEN");
        shift.setApprovedBy(supervisorId);
        shift.setApprovedAt(approvedAt);

        // 3. Save to database
        return shiftRepository.save(shift);
    }

    private String generateShiftNo(Long branchId) {
        return "SH-" + branchId + "-" + System.currentTimeMillis();
    }
}
