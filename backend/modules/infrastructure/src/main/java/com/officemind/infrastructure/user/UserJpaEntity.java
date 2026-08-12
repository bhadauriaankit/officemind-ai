package com.officemind.infrastructure.user;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "officemind")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(name = "keycloak_subject_id", nullable = false, unique = true)
    private String keycloakSubjectId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    private String department;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", schema = "officemind", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<RoleJpa> roles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusJpa status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserJpaEntity() {
    }

    public UserJpaEntity(UUID id, String keycloakSubjectId, String email, String displayName,
                          String department, Set<RoleJpa> roles, StatusJpa status,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.keycloakSubjectId = keycloakSubjectId;
        this.email = email;
        this.displayName = displayName;
        this.department = department;
        this.roles = roles;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public enum RoleJpa { ADMIN, HR, FINANCE, IT, DEVELOPER, EMPLOYEE }
    public enum StatusJpa { ACTIVE, DISABLED }

    public UUID getId() { return id; }
    public String getKeycloakSubjectId() { return keycloakSubjectId; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getDepartment() { return department; }
    public Set<RoleJpa> getRoles() { return roles; }
    public StatusJpa getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
