package com.officemind.infrastructure.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByKeycloakSubjectId(String keycloakSubjectId);

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserJpaEntity> findAll(Pageable pageable);
}
