package com.officemind.application.notification;

import com.officemind.domain.notification.Notification;
import com.officemind.domain.notification.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class CreateNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;

    public CreateNotificationUseCase(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification execute(String userId, NotificationType type, String title, String body) {
        return notificationRepository.save(Notification.create(userId, type, title, body));
    }
}
