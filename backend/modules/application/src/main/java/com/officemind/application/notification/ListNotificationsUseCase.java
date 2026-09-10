package com.officemind.application.notification;

import com.officemind.domain.notification.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListNotificationsUseCase {

    private final NotificationRepositoryPort notificationRepository;

    public ListNotificationsUseCase(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> execute(String userId, boolean unreadOnly) {
        return notificationRepository.findByUserId(userId, unreadOnly);
    }
}
