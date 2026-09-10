package com.officemind.api.notification;

import com.officemind.domain.notification.Notification;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String body,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId().value(), n.getType().name(),
                n.getTitle(), n.getBody(), n.isRead(), n.getCreatedAt());
    }
}
