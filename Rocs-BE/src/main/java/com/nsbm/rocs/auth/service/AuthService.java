package com.nsbm.rocs.auth.service;

import com.nsbm.rocs.auth.service.JwtService;
import com.nsbm.rocs.auth.dto.LogInResponseDTO;
import com.nsbm.rocs.auth.dto.RegisterRequestDTO;
import com.nsbm.rocs.auth.dto.RegisterResponseDTO;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.enums.AccountStatus;
import com.nsbm.rocs.entity.enums.Role;
import com.nsbm.rocs.auth.repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserProfileRepo userProfileRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserProfileRepo userProfileRepo, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userProfileRepo = userProfileRepo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserProfile findByEmail(String email) {
        Optional<UserProfile> userProfile = userProfileRepo.findByEmail(email);
        return userProfile.orElse(null);
    }

    public UserProfile findByUsername(String username) {
        Optional<UserProfile> userProfile = userProfileRepo.findByUsername(username);
        return userProfile.orElse(null);
    }

    //    register user
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {

        UserProfile existUserByEmail = findByEmail(registerRequestDTO.getEmail());
        if (existUserByEmail != null) {
            return new RegisterResponseDTO("User with this email already exists");
        }
        UserProfile existUserByUsername = findByUsername(registerRequestDTO.getUsername());
        if (existUserByUsername != null) {
            return new RegisterResponseDTO("User with this username already exists");
        }

        UserProfile userProfile = new UserProfile();

        userProfile.setFullName(registerRequestDTO.getFullName());
        userProfile.setUsername(registerRequestDTO.getUsername());
        userProfile.setEmail(registerRequestDTO.getEmail());
        userProfile.setPassword(bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()));
        userProfile.setRole(Role.ADMIN);
        userProfile.setAccountStatus(AccountStatus.ACTIVE);
        UserProfile registerUser = userProfileRepo.save(userProfile);

        return new RegisterResponseDTO(
                registerUser.getUserId(),
                registerUser.getEmail(),
                registerUser.getFullName(),
                registerUser.getRole(),
                registerUser.getAccountStatus(),
                "User registered successfully"
        );
    }

    public LogInResponseDTO logInUser(String username, String password) {
        UserProfile existUserByUsername = findByUsername(username);
        if (existUserByUsername == null) {
            return new LogInResponseDTO("Invalid username");
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            Map<String,Object> claims = new HashMap<>();
            claims.put("role", Role.ADMIN);
            claims.put("username", existUserByUsername.getUsername());
            claims.put("email", existUserByUsername.getEmail());
            claims.put("userId", existUserByUsername.getUserId());

            String token = jwtService.generateToken(claims,existUserByUsername);
            return new LogInResponseDTO(
                    existUserByUsername.getUserId(),
                    existUserByUsername.getUsername(),
                    existUserByUsername.getEmail(),
                    token,
                    existUserByUsername.getRole(),
                    "Login successful"
            );
        } else {
            return new LogInResponseDTO("Invalid credentials");
        }
    }
}
