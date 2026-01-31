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
    private Boolean isActive;
    private Boolean isVerified;
    private Boolean blacklisted;
    private String blacklistReason;
    private String notes;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<SupplierContactDTO> contacts = new ArrayList<>();
    private List<SupplierBranchDTO> branches = new ArrayList<>();
}

