package com.officemind.infrastructure.aisettings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface AiSettingsJpaRepository extends JpaRepository<AiSettingsJpaEntity, UUID> {
}
