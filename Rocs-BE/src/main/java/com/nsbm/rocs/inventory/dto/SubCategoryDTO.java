package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO {

    private Long subcategoryId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Subcategory name is required")
    @Size(max = 100, message = "Subcategory name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean isActive = true;

    private String categoryName; // For display purposes
}

