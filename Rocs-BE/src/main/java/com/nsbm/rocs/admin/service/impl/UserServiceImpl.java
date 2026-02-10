package com.nsbm.rocs.admin.service.impl;

import com.nsbm.rocs.admin.dto.UserDTO;
import com.nsbm.rocs.admin.service.UserService;
import com.nsbm.rocs.entity.enums.AccountStatus;
import com.nsbm.rocs.entity.enums.Role;
import com.nsbm.rocs.entity.main.Branch;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.repository.BranchRepository;
import com.nsbm.rocs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                         BranchRepository branchRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long getAllUserCount() {
        return userRepository.count();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllUsers();
        }
        String lowerQuery = query.toLowerCase();
        return userRepository.findAll().stream()
                .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(lowerQuery)) ||
                             (u.getFullName() != null && u.getFullName().toLowerCase().contains(lowerQuery)) ||
                             (u.getEmployeeId() != null && u.getEmployeeId().toLowerCase().contains(lowerQuery)))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO registerManager(UserDTO userDTO) {
        // Basic validation
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByEmployeeId(userDTO.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        UserProfile user = new UserProfile();
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setEmployeeId(userDTO.getEmployeeId());
        user.setRole(Role.BRANCH_MANAGER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        
        // Generate a random password if not provided
        String rawPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(rawPassword));
        
        if (userDTO.getBranchId() != null) {
            Branch branch = branchRepository.findById(userDTO.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            user.setBranch(branch);
        }

        UserProfile savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getEmployeeId() != null) user.setEmployeeId(userDTO.getEmployeeId());
        
        if (userDTO.getBranchId() != null) {
            Branch branch = branchRepository.findById(userDTO.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            user.setBranch(branch);
        }

        UserProfile updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            user.setAccountStatus(AccountStatus.SUSPENDED); // Or INACTIVE
        } else {
            user.setAccountStatus(AccountStatus.ACTIVE);
        }
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> getManagers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.BRANCH_MANAGER)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(UserProfile user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setStatus(user.getAccountStatus() != null ? user.getAccountStatus().name() : null);
        
        if (user.getBranch() != null) {
            dto.setBranchId(user.getBranch().getBranchId());
            dto.setBranchName(user.getBranch().getName());
        }
        return dto;
    }
}
