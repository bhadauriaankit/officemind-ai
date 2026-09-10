package com.officemind.api.notification;

import com.officemind.application.notification.ListNotificationsUseCase;
import com.officemind.application.notification.MarkNotificationReadUseCase;
import com.officemind.application.user.UserRepositoryPort;
import com.officemind.domain.notification.Notification;
import com.officemind.domain.shared.EntityId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final ListNotificationsUseCase listNotificationsUseCase;
    private final MarkNotificationReadUseCase markReadUseCase;
    private final UserRepositoryPort userRepository;

    public NotificationController(ListNotificationsUseCase listNotificationsUseCase,
                                   MarkNotificationReadUseCase markReadUseCase,
                                   UserRepositoryPort userRepository) {
        this.listNotificationsUseCase = listNotificationsUseCase;
        this.markReadUseCase = markReadUseCase;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<NotificationResponse> list(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            JwtAuthenticationToken authentication) {
        String userId = resolveUserId(authentication);
        return listNotificationsUseCase.execute(userId, unreadOnly).stream()
                .map(NotificationResponse::from).toList();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable UUID id) {
        Notification updated = markReadUseCase.execute(EntityId.of(id));
        return ResponseEntity.ok(NotificationResponse.from(updated));
    }

    private String resolveUserId(JwtAuthenticationToken authentication) {
        return userRepository.findByKeycloakSubjectId(authentication.getToken().getSubject())
                .map(u -> u.getId().value().toString())
                .orElseThrow(() -> new IllegalStateException("User not provisioned"));
    }
}
