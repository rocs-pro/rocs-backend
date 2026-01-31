package com.nsbm.rocs.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SupplierRequestDTO {

    @NotBlank(message = "Supplier code is required")
    @Size(max = 60, message = "Supplier code must not exceed 60 characters")
    private String code;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150, message = "Supplier name must not exceed 150 characters")
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
    private String state;
    private String postalCode;
    private String country;
    private String taxId;
    private String vatNumber;
    private String businessRegistrationNo;
    private Integer creditDays;
    private BigDecimal creditLimit;
    private String paymentTerms;
    private String bankName;
    private String bankAccountNo;
    private String bankBranch;
    private String supplierType;
    private String supplierCategory;
    private Integer rating;
    private Boolean isActive = true;
    private Boolean isVerified = false;
    private Boolean blacklisted = false;
    private String blacklistReason;
    private String notes;
    private Long createdBy;

    @Valid
    private List<SupplierContactDTO> contacts = new ArrayList<>();

    @Valid
    private List<SupplierBranchDTO> branches = new ArrayList<>();
}

