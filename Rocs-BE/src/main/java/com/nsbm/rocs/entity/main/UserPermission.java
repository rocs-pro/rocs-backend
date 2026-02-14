package com.nsbm.rocs.entity.main;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_permissions")
@Getter
@Setter
@NoArgsConstructor
@IdClass(UserPermission.UserPermissionId.class)
public class UserPermission {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "granted_by")
    private Long grantedBy;

    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    @PrePersist
    protected void onCreate() {
        grantedAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserPermissionId implements Serializable {
        private Long userId;
        private Long permissionId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserPermissionId that = (UserPermissionId) o;
            return userId.equals(that.userId) && permissionId.equals(that.permissionId);
        }

        @Override
        public int hashCode() {
            return 31 * userId.hashCode() + permissionId.hashCode();
        }
    }
}

