package com.nsbm.rocs.entity.inventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "InventorySupplier")
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "code", nullable = false, unique = true, length = 60)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "mobile", length = 50)
    private String mobile;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "website", length = 150)
    private String website;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country = "Sri Lanka";

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "vat_number", length = 100)
    private String vatNumber;

    @Column(name = "business_registration_no", length = 100)
    private String businessRegistrationNo;

    @Column(name = "credit_days")
    private Integer creditDays;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "payment_terms", length = 255)
    private String paymentTerms;

    @Column(name = "bank_name", length = 150)
    private String bankName;

    @Column(name = "bank_account_no", length = 100)
    private String bankAccountNo;

    @Column(name = "bank_branch", length = 150)
    private String bankBranch;

    @Column(name = "supplier_type", length = 100)
    private String supplierType;

    @Column(name = "supplier_category", length = 100)
    private String supplierCategory;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "blacklisted")
    private Boolean blacklisted = false;

    @Column(name = "blacklist_reason", columnDefinition = "TEXT")
    private String blacklistReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierContact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierBranch> branches = new ArrayList<>();
}

