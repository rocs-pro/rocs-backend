package com.nsbm.rocs.entity.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "branch_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "terminal_id")
    private Long terminalId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action_type", nullable = false)
    private String actionType; // LOGIN, SALE, RETURN, etc.

    @Column(name = "entity_type")
    private String entityType; // SALE, PRODUCT, STOCK

    @Column(name = "entity_id")
    private Long entityId; // Changed to Long as per SQL schema

    @Column(name = "description", length = 500)
    private String details;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(length = 20)
    private String severity = "INFO"; // INFO, WARNING, CRITICAL

    @Column(length = 20)
    private String status = "SUCCESS"; // SUCCESS, FAILED

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    // Transient fields for service layer convenience if needed
    @Transient
    private String username; 
    @Transient
    private String userRole;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (severity == null) severity = "INFO";
        if (status == null) status = "SUCCESS";
    }
}
