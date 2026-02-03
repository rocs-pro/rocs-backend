package com.nsbm.rocs.dashboard.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {

    private Long id;

    private String name;
    private String code;
    private String address;
    private String location;
    private String phone;
    private String email;
    private Boolean isActive;
}
