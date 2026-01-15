package com.nsbm.rocs.dashboard.controller;

import com.nsbm.rocs.dashboard.dto.UserDTO;
import com.nsbm.rocs.dashboard.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard/usermanagement")
public class UserManagementController {

    @Autowired
    private UserServiceImpl userService;

    // GET /api/dashboard/usermanagement
    @GetMapping
    public List<UserDTO> listAll() {
        return userService.getAllUsers();
    }

    // GET /api/dashboard/usermanagement/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        UserDTO dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    // POST /api/dashboard/usermanagement
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
        UserDTO created = userService.createUser(userDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/dashboard/usermanagement/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/dashboard/usermanagement/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
