package com.officemind.api.conversation;

import com.officemind.application.conversation.GetConversationUseCase;
import com.officemind.application.conversation.ListConversationsUseCase;
import com.officemind.application.conversation.SendMessageUseCase;
import com.officemind.application.user.UserRepositoryPort;
import com.officemind.api.user.PageResponse;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.shared.EntityId;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final SendMessageUseCase sendMessageUseCase;
    private final ListConversationsUseCase listConversationsUseCase;
    private final GetConversationUseCase getConversationUseCase;
    private final UserRepositoryPort userRepository;

    public ConversationController(SendMessageUseCase sendMessageUseCase,
                                   ListConversationsUseCase listConversationsUseCase,
                                   GetConversationUseCase getConversationUseCase,
                                   UserRepositoryPort userRepository) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.listConversationsUseCase = listConversationsUseCase;
        this.getConversationUseCase = getConversationUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ConversationResponse start(@Valid @RequestBody SendMessageRequest request,
                                       JwtAuthenticationToken authentication) {
        String internalUserId = resolveInternalUserId(authentication);
        Conversation conversation = sendMessageUseCase.startConversation(internalUserId, request.message());
        return ConversationResponse.from(conversation);
    }

    @PostMapping("/{id}/messages")
    public ConversationResponse continueConversation(@PathVariable UUID id,
                                                       @Valid @RequestBody SendMessageRequest request) {
        Conversation conversation = sendMessageUseCase.continueConversation(EntityId.of(id), request.message());
        return ConversationResponse.from(conversation);
    }

    @GetMapping("/{id}")
    public ConversationResponse get(@PathVariable UUID id) {
        return ConversationResponse.from(getConversationUseCase.execute(EntityId.of(id)));
    }

    @GetMapping
    public PageResponse<ConversationResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            JwtAuthenticationToken authentication) {
        String internalUserId = resolveInternalUserId(authentication);
        return PageResponse.from(
                listConversationsUseCase.execute(internalUserId, page, size),
                ConversationResponse::from
        );
    }

    private String resolveInternalUserId(JwtAuthenticationToken authentication) {
        String keycloakSubjectId = authentication.getToken().getSubject();
        return userRepository.findByKeycloakSubjectId(keycloakSubjectId)
                .map(u -> u.getId().value().toString())
                .orElseThrow(() -> new IllegalStateException("User not yet provisioned"));
    }
}
