package com.nsbm.rocs.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SupplierResponseDTO {

    private Long supplierId;
    private String code;
    private String name;
    private String companyName;
    private String contactPerson;
    private String phone;
    private String mobile;
    private String email;
    private String website;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
    private String taxId;
    private Integer creditDays;
    private BigDecimal creditLimit;
    private Boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SupplierContactDTO> contacts = new ArrayList<>();
    private List<SupplierBranchDTO> branches = new ArrayList<>();
}

