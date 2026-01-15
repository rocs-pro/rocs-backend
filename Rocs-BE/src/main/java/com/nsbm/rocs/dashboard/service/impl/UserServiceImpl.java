package com.nsbm.rocs.dashboard.service.impl;

import com.nsbm.rocs.dashboard.dto.UserDTO;
import com.nsbm.rocs.dashboard.repo.UserRepo;
import com.nsbm.rocs.dashboard.service.UserService;
import com.nsbm.rocs.entity.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ModelMapper modelMapper;

    // List all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> modelMapper.map(u, UserDTO.class))
                .collect(Collectors.toList());
    }

    // Get by id
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return modelMapper.map(user, UserDTO.class);
    }

    // Create
    public UserDTO createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserDTO.class);
    }

    // Update (partial - ignore nulls from DTO)
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(userDTO, existing);
        // reset condition to avoid side-effects if mapper is shared
        modelMapper.getConfiguration().setPropertyCondition(null);
        User saved = userRepository.save(existing);
        return modelMapper.map(saved, UserDTO.class);
    }

    // Delete
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }
}