package com.nsbm.rocs.auth.dto;

import com.nsbm.rocs.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogInResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String token;
    private Role role;
    private String redirectPath;
    private String message;

    public LogInResponseDTO(String message) {
        this.message = message;
    }

    public LogInResponseDTO(Long userId, String username, String email, String token, Role role, String message) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = token;
        this.role = role;
        this.redirectPath = determineRedirectPath(role);
        this.message = message;
    }

    public static String determineRedirectPath(Role role) {
        if (role == null) {
            return "/pending-approval";
        }
        return switch (role) {
            case ADMIN, BRANCH_MANAGER -> "/dashboard";
            case CASHIER, SUPERVISOR -> "/pos";
            case STORE_KEEPER -> "/inventory";
        };
    }
}
