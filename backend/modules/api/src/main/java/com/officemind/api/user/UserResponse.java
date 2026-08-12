package com.officemind.api.user;

import com.officemind.domain.user.RoleName;
import com.officemind.domain.user.User;
import com.officemind.domain.user.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        String department,
        Set<RoleName> roles,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId().value(),
                user.getEmail(),
                user.getDisplayName(),
                user.getDepartment(),
                user.getRoles(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
