package com.officemind.application.notification;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.notification.Notification;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class MarkNotificationReadUseCase {

    private final NotificationRepositoryPort notificationRepository;

    public MarkNotificationReadUseCase(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification execute(EntityId notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notification.markRead();
        return notificationRepository.save(notification);
    }
}
