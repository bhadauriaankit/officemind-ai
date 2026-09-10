package com.officemind.infrastructure.notification;

import com.officemind.application.notification.NotificationRepositoryPort;
import com.officemind.domain.notification.Notification;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository jpaRepository;

    public NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        return NotificationMapper.toDomain(jpaRepository.save(NotificationMapper.toJpa(notification)));
    }

    @Override
    public List<Notification> findByUserId(String userId, boolean unreadOnly) {
        UUID uid = UUID.fromString(userId);
        List<NotificationJpaEntity> entities = unreadOnly
                ? jpaRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(uid)
                : jpaRepository.findByUserIdOrderByCreatedAtDesc(uid);
        return entities.stream().map(NotificationMapper::toDomain).toList();
    }

    @Override
    public Optional<Notification> findById(EntityId id) {
        return jpaRepository.findById(id.value()).map(NotificationMapper::toDomain);
    }
}
