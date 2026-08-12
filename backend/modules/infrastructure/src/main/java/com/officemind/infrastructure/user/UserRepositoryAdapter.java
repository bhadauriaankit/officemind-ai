package com.officemind.infrastructure.user;

import com.officemind.application.user.UserRepositoryPort;
import com.officemind.common.paging.PageResult;
import com.officemind.domain.shared.EntityId;
import com.officemind.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = jpaRepository.save(UserMapper.toJpa(user));
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(EntityId id) {
        return jpaRepository.findById(id.value()).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByKeycloakSubjectId(String keycloakSubjectId) {
        return jpaRepository.findByKeycloakSubjectId(keycloakSubjectId).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public PageResult<User> findAll(int page, int size) {
        Page<UserJpaEntity> result = jpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(UserMapper::toDomain).toList(),
                page,
                size,
                result.getTotalElements()
        );
    }
}
