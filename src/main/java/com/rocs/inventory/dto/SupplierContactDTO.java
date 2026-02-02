package com.rocs.inventory.dto;

public class SupplierContactDTO {
    private Long contactId;
    private String name;
    private String designation;
    private String phone;
    // mobile field removed - not in database schema
    private String email;
    private Boolean isPrimary;

    // getters and setters
    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // mobile getter/setter removed

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}

