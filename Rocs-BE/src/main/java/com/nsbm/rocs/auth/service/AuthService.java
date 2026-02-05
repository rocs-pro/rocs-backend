package com.nsbm.rocs.auth.service;

import com.nsbm.rocs.auth.dto.LogInResponseDTO;
import com.nsbm.rocs.auth.dto.RegisterRequestDTO;
import com.nsbm.rocs.auth.dto.RegisterResponseDTO;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.main.Branch;
import com.nsbm.rocs.entity.enums.AccountStatus;
import com.nsbm.rocs.entity.audit.Approval;
import com.nsbm.rocs.auth.repo.UserProfileRepo;
import com.nsbm.rocs.auth.repo.BranchRepo;
import com.nsbm.rocs.repository.audit.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserProfileRepo userProfileRepo;
    private final BranchRepo branchRepo;
    private final ApprovalRepository approvalRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserProfileRepo userProfileRepo, BranchRepo branchRepo, ApprovalRepository approvalRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userProfileRepo = userProfileRepo;
        this.branchRepo = branchRepo;
        this.approvalRepository = approvalRepository;
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

    public List<Branch> getAllBranches() {
        return branchRepo.findAll();
    }

    //    register user
    @Transactional
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {

        UserProfile existUserByEmail = findByEmail(registerRequestDTO.getEmail());
        if (existUserByEmail != null) {
            return new RegisterResponseDTO("EMAIL: User with this email already exists");
        }
        UserProfile existUserByUsername = findByUsername(registerRequestDTO.getUsername());
        if (existUserByUsername != null) {
            return new RegisterResponseDTO("USERNAME: User with this username already exists");
        }

        Optional<UserProfile> existUserByPhone = userProfileRepo.findByPhone(registerRequestDTO.getPhone());
        if (existUserByPhone.isPresent()) {
            return new RegisterResponseDTO("PHONE: User with this phone number already exists");
        }

        Optional<UserProfile> existUserByEmployeeId = userProfileRepo.findByEmployeeId(registerRequestDTO.getEmployeeId());
        if (existUserByEmployeeId.isPresent()) {
            return new RegisterResponseDTO("EMPLOYEE_ID: User with this Employee ID already exists");
        }

        Branch branch = branchRepo.findById(registerRequestDTO.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        UserProfile userProfile = new UserProfile();

        userProfile.setFullName(registerRequestDTO.getFullName());
        userProfile.setUsername(registerRequestDTO.getUsername());
        userProfile.setEmail(registerRequestDTO.getEmail());
        userProfile.setPassword(bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()));

        // Extended profile fields
        userProfile.setPhone(registerRequestDTO.getPhone());
        userProfile.setEmployeeId(registerRequestDTO.getEmployeeId());
        userProfile.setBranch(branch);

        // Role is assigned by Admin later, so it remains null initially.
        userProfile.setRole(null);
        // Set account status to PENDING explicitly
        userProfile.setAccountStatus(AccountStatus.PENDING);

        UserProfile registerUser = userProfileRepo.save(userProfile);

        // Create Approval Request
        Approval approval = new Approval();
        approval.setBranchId(branch.getBranchId());
        approval.setType("USER_REGISTRATION");
        approval.setReferenceId(registerUser.getUserId());
        approval.setReferenceNo("USER-" + registerUser.getUsername());
        approval.setStatus("PENDING");
        approval.setRequestedBy(registerUser.getUserId());
        approval.setRequestNotes("User registration for " + registerUser.getFullName());
        approvalRepository.save(approval);

        return new RegisterResponseDTO(
                registerUser.getUserId(),
                registerUser.getEmail(),
                registerUser.getFullName(),
                registerUser.getRole(),
                registerUser.getAccountStatus(),
                "User registered successfully. Pending Admin approval."
        );
    }

    public LogInResponseDTO logInUser(String username, String password) {
        UserProfile existUserByUsername = findByUsername(username);
        if (existUserByUsername == null) {
            return new LogInResponseDTO("Invalid username");
        }

        // Check if account is pending approval
        if (existUserByUsername.getAccountStatus() == AccountStatus.PENDING) {
            return new LogInResponseDTO("Account pending approval. Please wait for admin to activate your account.");
        }

        // Check if account is rejected
        if (existUserByUsername.getAccountStatus() == AccountStatus.REJECTED) {
            return new LogInResponseDTO("Account has been rejected. Please contact administrator.");
        }

        // Check if account is suspended
        if (existUserByUsername.getAccountStatus() == AccountStatus.SUSPENDED) {
            return new LogInResponseDTO("Account is suspended. Please contact administrator.");
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            Map<String,Object> claims = new HashMap<>();
            claims.put("role", existUserByUsername.getRole() == null ? "PENDING" : existUserByUsername.getRole().name());
            claims.put("username", existUserByUsername.getUsername());
            claims.put("email", existUserByUsername.getEmail());
            claims.put("userId", existUserByUsername.getUserId());
            if (existUserByUsername.getBranch() != null) {
                claims.put("branchId", existUserByUsername.getBranch().getBranchId());
            }

            String token = jwtService.generateToken(claims, existUserByUsername);
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

    public boolean verifySupervisor(String username, String password) {
        UserProfile user = findByUsername(username);
        if (user == null) {
            return false;
        }

        // Active check
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            return false;
        }

        // Role check
        if (user.getRole() == null) return false;

        String roleName = user.getRole().name();

        boolean isSupervisor = roleName.equals("ADMIN") ||
                               roleName.equals("BRANCH_MANAGER") ||
                               roleName.equals("SUPERVISOR") ||
                               roleName.equals("MANAGER");

        if (!isSupervisor) return false;

        // Password check
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }
}
