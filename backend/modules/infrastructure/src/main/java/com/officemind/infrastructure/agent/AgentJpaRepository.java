package com.officemind.infrastructure.agent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface AgentJpaRepository extends JpaRepository<AgentJpaEntity, UUID> {
    Optional<AgentJpaEntity> findByKey(String key);
}