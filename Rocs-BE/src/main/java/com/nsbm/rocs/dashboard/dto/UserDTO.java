package com.nsbm.rocs.dashboard.dto;

import com.nsbm.rocs.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private Long userId;
    private String userName;
    private  String password;
    private String fullName;
    private String email;
    private String status;
    private Role role;
}
