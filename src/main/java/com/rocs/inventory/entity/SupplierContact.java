package com.rocs.inventory.entity;
}
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    public Boolean getIsPrimary() { return isPrimary; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getMobile() { return mobile; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setDesignation(String designation) { this.designation = designation; }
    public String getDesignation() { return designation; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public Supplier getSupplier() { return supplier; }

    public void setContactId(Long contactId) { this.contactId = contactId; }
    public Long getContactId() { return contactId; }
    // getters and setters

    private LocalDateTime createdAt;
    @Column(name = "created_at")

    private Boolean isPrimary = false;
    @Column(name = "is_primary")

    private String email;
    @Column(name = "email")

    private String mobile;
    @Column(name = "mobile")

    private String phone;
    @Column(name = "phone")

    private String designation;
    @Column(name = "designation")

    private String name;
    @Column(name = "name", nullable = false)

    private Supplier supplier;
    @JoinColumn(name = "supplier_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)

    private Long contactId;
    @Column(name = "contact_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
public class SupplierContact {
@Table(name = "supplier_contacts")
@Entity

import java.time.LocalDateTime;
import javax.persistence.*;


