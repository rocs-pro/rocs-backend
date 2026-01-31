package com.nsbm.rocs.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierContactDTO {

    private Long contactId;

    @NotBlank(message = "Contact name is required")
    @Size(max = 150, message = "Contact name must not exceed 150 characters")
    private String name;

    @Size(max = 150, message = "Designation must not exceed 150 characters")
    private String designation;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;


    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private Boolean isPrimary = false;
}

