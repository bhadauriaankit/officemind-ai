package com.officemind.infrastructure.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {
    List<NotificationJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<NotificationJpaEntity> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);
}
