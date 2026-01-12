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
    private String message;

    public LogInResponseDTO(String message) {
        this.message = message;
    }
}
