package com.nsbm.rocs.auth.controller;

import com.nsbm.rocs.auth.dto.*;
import com.nsbm.rocs.auth.service.AuthService;
import com.nsbm.rocs.entity.main.Branch;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@NullMarked
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO userDetails) {
        RegisterResponseDTO registeredUser = authService.registerUser(userDetails);
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
}