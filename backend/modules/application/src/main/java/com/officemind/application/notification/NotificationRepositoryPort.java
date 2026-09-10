package com.officemind.application.notification;

import com.officemind.domain.notification.Notification;
import com.officemind.domain.shared.EntityId;

import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    List<Notification> findByUserId(String userId, boolean unreadOnly);
    Optional<Notification> findById(EntityId id);
}
