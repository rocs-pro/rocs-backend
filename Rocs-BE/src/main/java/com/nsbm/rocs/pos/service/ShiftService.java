package com.nsbm.rocs.pos.service;

import com.nsbm.rocs.auth.repo.UserProfileRepo;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.pos.CashShift;
import com.nsbm.rocs.pos.dto.ShiftStartRequest;
import com.nsbm.rocs.pos.dto.CashFlowRequest;
import com.nsbm.rocs.pos.dto.shift.CloseShiftRequest;
import com.nsbm.rocs.pos.repository.ShiftRepository;
import com.nsbm.rocs.pos.repository.SaleRepository;
import com.nsbm.rocs.pos.repository.CashFlowRepository;
import com.nsbm.rocs.pos.repository.PaymentRepository;
import com.nsbm.rocs.entity.pos.CashFlow;
import com.nsbm.rocs.entity.pos.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final AuthenticationManager authenticationManager;
    private final UserProfileRepo userProfileRepo;
    private final SaleRepository saleRepository;
    private final CashFlowRepository cashFlowRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public ShiftService(ShiftRepository shiftRepository,
                        AuthenticationManager authenticationManager,
                        UserProfileRepo userProfileRepo,
                        SaleRepository saleRepository,
                        CashFlowRepository cashFlowRepository,
                        PaymentRepository paymentRepository) {
        this.shiftRepository = shiftRepository;
        this.authenticationManager = authenticationManager;
        this.userProfileRepo = userProfileRepo;
        this.saleRepository = saleRepository;
        this.cashFlowRepository = cashFlowRepository;
        this.paymentRepository = paymentRepository;
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

    public Map<String, Object> getShiftTotals(Long shiftId) {
        BigDecimal totalSales = saleRepository.sumNetTotalByShiftId(shiftId);
        List<CashFlow> flows = cashFlowRepository.findByShiftId(shiftId);
        BigDecimal paidIn = flows.stream().filter(f -> "PAID_IN".equals(f.getType())).map(CashFlow::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paidOut = flows.stream().filter(f -> "PAID_OUT".equals(f.getType())).map(CashFlow::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<CashShift> shift = shiftRepository.findById(shiftId);
        BigDecimal openingCash = shift.map(CashShift::getOpeningCash).orElse(BigDecimal.ZERO);

        // Detailed breakdown
        List<Payment> payments = paymentRepository.findByShiftId(shiftId);

        BigDecimal cashSales = payments.stream()
                .filter(p -> "CASH".equalsIgnoreCase(p.getPaymentType()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cardTotal = payments.stream()
                .filter(p -> "CARD".equalsIgnoreCase(p.getPaymentType()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cardCount = payments.stream()
                 .filter(p -> "CARD".equalsIgnoreCase(p.getPaymentType()))
                 .count();

        // QR Support
        BigDecimal qrTotal = payments.stream()
                .filter(p -> "QR".equalsIgnoreCase(p.getPaymentType()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> bankBreakdown = payments.stream()
                .filter(p -> "CARD".equalsIgnoreCase(p.getPaymentType()))
                .collect(Collectors.groupingBy(
                        p -> p.getBankName() != null ? p.getBankName() : "Unknown",
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));

        BigDecimal expectedCash = openingCash.add(cashSales).add(paidIn).subtract(paidOut);

        return Map.of(
            "totalSales", totalSales,
            "cashSales", cashSales,
            "qrTotal", qrTotal,
            "paidIn", paidIn,
            "paidOut", paidOut,
            "openingFloat", openingCash,
            "expectedCash", expectedCash,
            "cardTotal", cardTotal,
            "cardCount", cardCount,
            "bankBreakdown", bankBreakdown
        );
    }

    @Transactional
    public void closeShift(Long cashierId, CloseShiftRequest request) {
        // 1. Supervisor Validation
        if (request.getSupervisorUsername() == null || request.getSupervisorPassword() == null) {
            throw new IllegalArgumentException("Supervisor credentials required");
        }

        Character loginResult = validateSupervisor(request.getSupervisorUsername(), request.getSupervisorPassword());
        if(loginResult == null) {
             throw new IllegalArgumentException("Invalid supervisor credentials");
        }

        // 2. Find Active Shift
        CashShift shift = shiftRepository.findActiveShiftByCashier(cashierId)
                .orElseThrow(() -> new IllegalStateException("No active shift found for cashier"));

        // 3. Calculate Totals
        Map<String, Object> totals = getShiftTotals(shift.getShiftId());
        BigDecimal expectedCash = (BigDecimal) totals.get("expectedCash");
        BigDecimal totalSales = (BigDecimal) totals.get("totalSales");
        // BigDecimal totalReturns = ...;

        // 4. Update Shift
        shift.setClosedAt(LocalDateTime.now());
        shift.setClosingCash(request.getClosingCash());
        shift.setExpectedCash(expectedCash);
        shift.setCashDifference(request.getClosingCash().subtract(expectedCash));
        shift.setTotalSales(totalSales);
        shift.setStatus("CLOSED");
        shift.setNotes(request.getNotes());
        // shift.setApprovedBy(...);

        shiftRepository.update(shift);
    }

    private Character validateSupervisor(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            return auth.isAuthenticated() ? 'Y' : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public CashFlow recordCashFlow(Long cashierId, CashFlowRequest request) {
        // 1. Find Active Shift
        CashShift shift = shiftRepository.findActiveShiftByCashier(cashierId)
                .orElseThrow(() -> new IllegalStateException("No active shift found. Please open a shift first."));

        // 2. Create CashFlow
        CashFlow cashFlow = new CashFlow();
        cashFlow.setShiftId(shift.getShiftId());
        cashFlow.setType(request.getType());
        cashFlow.setAmount(request.getAmount());
        cashFlow.setReason(request.getReason());
        cashFlow.setReferenceNo(request.getReferenceNo());
        cashFlow.setCreatedBy(cashierId);

        // 3. Save
        return cashFlowRepository.save(cashFlow);
    }

    public Long getActiveShiftId(Long cashierId) {
        return shiftRepository.findActiveShiftByCashier(cashierId)
                .map(CashShift::getShiftId)
                .orElseThrow(() -> new IllegalStateException("No active shift found for cashier"));
    }
}
