package com.officemind.infrastructure.notification;

import com.officemind.domain.notification.Notification;
import com.officemind.domain.notification.NotificationType;
import com.officemind.domain.shared.EntityId;

final class NotificationMapper {
    private NotificationMapper() {}

    static Notification toDomain(NotificationJpaEntity e) {
        return Notification.rehydrate(
                EntityId.of(e.getId()),
                e.getUserId().toString(),
                NotificationType.valueOf(e.getType().name()),
                e.getTitle(), e.getBody(), e.isRead(), e.getCreatedAt());
    }

    static NotificationJpaEntity toJpa(Notification n) {
        return new NotificationJpaEntity(
                n.getId().value(),
                java.util.UUID.fromString(n.getUserId()),
                NotificationJpaEntity.TypeJpa.valueOf(n.getType().name()),
                n.getTitle(), n.getBody(), n.isRead(), n.getCreatedAt());
    }
}
