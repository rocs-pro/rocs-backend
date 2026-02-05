package com.nsbm.rocs.admin.controller;

import com.nsbm.rocs.admin.service.UserService;
import com.nsbm.rocs.admin.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * GET /api/admin/users/count
     * Returns total number of users.
     */
    @GetMapping("/count")
    public Long getAllUserCount() {
        return userService.getAllUserCount();
    }
}
