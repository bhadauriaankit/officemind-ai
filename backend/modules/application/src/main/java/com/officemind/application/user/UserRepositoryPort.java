package com.officemind.application.user;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(EntityId id);

    Optional<User> findByKeycloakSubjectId(String keycloakSubjectId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    PageResult<User> findAll(int page, int size);
}
