package com.officemind.infrastructure.user;

import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.RoleName;
import com.officemind.domain.user.User;
import com.officemind.domain.user.UserStatus;

import java.util.Set;
import java.util.stream.Collectors;

final class UserMapper {

    private UserMapper() {
    }

    static User toDomain(UserJpaEntity entity) {
        Set<RoleName> roles = entity.getRoles().stream()
                .map(r -> RoleName.valueOf(r.name()))
                .collect(Collectors.toSet());

        return User.rehydrate(
                EntityId.of(entity.getId()),
                entity.getKeycloakSubjectId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getDepartment(),
                roles,
                UserStatus.valueOf(entity.getStatus().name()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    static UserJpaEntity toJpa(User user) {
        Set<UserJpaEntity.RoleJpa> roles = user.getRoles().stream()
                .map(r -> UserJpaEntity.RoleJpa.valueOf(r.name()))
                .collect(Collectors.toSet());

        return new UserJpaEntity(
                user.getId().value(),
                user.getKeycloakSubjectId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getDepartment(),
                roles,
                UserJpaEntity.StatusJpa.valueOf(user.getStatus().name()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
