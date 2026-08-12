package com.officemind.domain.user;

import com.officemind.domain.shared.AggregateRoot;
import com.officemind.domain.shared.EntityId;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class User extends AggregateRoot {

    private final EntityId id;
    private final String keycloakSubjectId;
    private String email;
    private String displayName;
    private String department;
    private Set<RoleName> roles;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(EntityId id, String keycloakSubjectId, String email, String displayName,
                 String department, Set<RoleName> roles, UserStatus status,
                 Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.keycloakSubjectId = Objects.requireNonNull(keycloakSubjectId, "keycloakSubjectId is required");
        this.email = Objects.requireNonNull(email, "email is required");
        this.displayName = displayName;
        this.department = department;
        this.roles = EnumSet.copyOf(roles.isEmpty() ? Set.of(RoleName.defaultRole()) : roles);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User provisionFromIdentityProvider(String keycloakSubjectId, String email,
                                                       String displayName, Set<RoleName> rolesFromToken) {
        return new User(
                EntityId.generate(),
                keycloakSubjectId,
                email,
                displayName,
                null,
                rolesFromToken,
                UserStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );
    }

    public static User rehydrate(EntityId id, String keycloakSubjectId, String email, String displayName,
                                  String department, Set<RoleName> roles, UserStatus status,
                                  Instant createdAt, Instant updatedAt) {
        return new User(id, keycloakSubjectId, email, displayName, department, roles, status, createdAt, updatedAt);
    }

    public void changeRoles(Set<RoleName> newRoles) {
        if (newRoles.isEmpty()) {
            throw new IllegalArgumentException("a user must retain at least one role");
        }
        this.roles = EnumSet.copyOf(newRoles);
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
        this.updatedAt = Instant.now();
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(String displayName, String department) {
        this.displayName = displayName;
        this.department = department;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean hasRole(RoleName role) {
        return roles.contains(role);
    }

    public EntityId getId() { return id; }
    public String getKeycloakSubjectId() { return keycloakSubjectId; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getDepartment() { return department; }
    public Set<RoleName> getRoles() { return EnumSet.copyOf(roles); }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
