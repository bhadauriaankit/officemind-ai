package com.officemind.infrastructure.conversation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface ConversationJpaRepository extends JpaRepository<ConversationJpaEntity, UUID> {

    Page<ConversationJpaEntity> findAllByUserId(UUID userId, Pageable pageable);
}
