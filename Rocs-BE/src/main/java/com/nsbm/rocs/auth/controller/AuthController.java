package com.nsbm.rocs.auth.controller;

import com.nsbm.rocs.auth.dto.*;
import com.nsbm.rocs.auth.service.AuthService;
import com.nsbm.rocs.entity.main.Branch;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.audit.PasswordResetRequest;
import com.nsbm.rocs.repository.audit.PasswordResetRequestRepository;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@NullMarked
public class AuthController {

    private final AuthService authService;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public AuthController(AuthService authService, PasswordResetRequestRepository passwordResetRequestRepository) {
        this.authService = authService;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO userDetails) {
        RegisterResponseDTO registeredUser = authService.registerUser(userDetails);

        if (registeredUser.getUserId() == null && registeredUser.getMessage() != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO(registeredUser.getMessage()));
        }

        if (registeredUser == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorDTO("User registration unsuccessful"));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredUser);
    }

    @GetMapping("/branches")
    public ResponseEntity<List<Branch>> getBranches() {
        return ResponseEntity.ok(authService.getAllBranches());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInRequestDTO logInRequestDTO) {
        LogInResponseDTO responseDTO = authService.
                logInUser(logInRequestDTO.getUsername(), logInRequestDTO.getPassword());
        if (responseDTO == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDTO("Invalid credentials"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDTO);
        }
    }

    @PostMapping("/verify-supervisor")
    public ResponseEntity<?> verifySupervisor(@RequestBody LogInRequestDTO credentials) {
        boolean verified = authService.verifySupervisor(credentials.getUsername(), credentials.getPassword());
        if (verified) {
             return ResponseEntity.ok(Map.of("status", "verified", "message", "Supervisor verified successfully"));
        } else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDTO("Invalid supervisor credentials or insufficient permissions"));
        }
    }

    /**
     * POST /api/v1/auth/forgot-password
     * Public endpoint - user submits new password request which goes to admin for approval.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequestDTO request) {
        // Validate username exists
        UserProfile user = authService.findByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Username not found. Please check and try again."));
        }

        // Check if there's already a pending request for this user
        List<PasswordResetRequest> existing = passwordResetRequestRepository.findByUserId(user.getUserId());
        boolean hasPending = existing.stream().anyMatch(r -> "PENDING".equals(r.getStatus()));
        if (hasPending) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "You already have a pending password reset request. Please wait for admin approval."));
        }

        // Create the password reset request
        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setUserId(user.getUserId());
        resetRequest.setUsername(user.getUsername());
        resetRequest.setFullName(user.getFullName());
        resetRequest.setEmail(user.getEmail());
        resetRequest.setNewPasswordHash(bCryptPasswordEncoder.encode(request.getNewPassword()));
        resetRequest.setStatus("PENDING");
        resetRequest.setRequestNotes(request.getReason() != null ? request.getReason() : "Password reset requested");

        passwordResetRequestRepository.save(resetRequest);

        return ResponseEntity.ok(Map.of(
                "message", "Password reset request submitted successfully. Please wait for admin approval.",
                "status", "PENDING"
        ));
    }
}
