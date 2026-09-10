package com.officemind.api.conversation;

import com.officemind.application.conversation.DeleteConversationUseCase;
import com.officemind.application.conversation.GetConversationUseCase;
import com.officemind.application.conversation.ListConversationsUseCase;
import com.officemind.application.conversation.SendMessageUseCase;
import com.officemind.application.user.UserRepositoryPort;
import com.officemind.api.user.PageResponse;
import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.shared.EntityId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final SendMessageUseCase sendMessageUseCase;
    private final ListConversationsUseCase listConversationsUseCase;
    private final GetConversationUseCase getConversationUseCase;
    private final DeleteConversationUseCase deleteConversationUseCase;
    private final UserRepositoryPort userRepository;

    public ConversationController(SendMessageUseCase sendMessageUseCase,
                                   ListConversationsUseCase listConversationsUseCase,
                                   GetConversationUseCase getConversationUseCase,
                                   DeleteConversationUseCase deleteConversationUseCase,
                                   UserRepositoryPort userRepository) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.listConversationsUseCase = listConversationsUseCase;
        this.getConversationUseCase = getConversationUseCase;
        this.deleteConversationUseCase = deleteConversationUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ConversationResponse start(@Valid @RequestBody SendMessageRequest request,
                                       JwtAuthenticationToken authentication) {
        String internalUserId = resolveInternalUserId(authentication);
        EntityId agentId = request.agentId() != null && !request.agentId().isBlank()
                ? EntityId.of(UUID.fromString(request.agentId()))
                : null;
        Conversation conversation = sendMessageUseCase.startConversation(internalUserId, request.message(), agentId);
        return ConversationResponse.from(conversation);
    }

    @PostMapping("/{id}/messages")
    public ConversationResponse continueConversation(@PathVariable UUID id,
                                                       @Valid @RequestBody SendMessageRequest request,
                                                       JwtAuthenticationToken authentication) {
        String internalUserId = resolveInternalUserId(authentication);
        Conversation existing = getConversationUseCase.execute(EntityId.of(id));
        if (!existing.getUserId().equals(internalUserId)) {
            throw new ResourceNotFoundException("Conversation", id);
        }
        Conversation conversation = sendMessageUseCase.continueConversation(EntityId.of(id), request.message());
        return ConversationResponse.from(conversation);
    }

    @GetMapping("/{id}")
    public ConversationResponse get(@PathVariable UUID id, JwtAuthenticationToken authentication) {
        String internalUserId = resolveInternalUserId(authentication);
        Conversation existing = getConversationUseCase.execute(EntityId.of(id));
        if (!existing.getUserId().equals(internalUserId)) {
            throw new ResourceNotFoundException("Conversation", id);
        }
        return ConversationResponse.from(existing);
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

    /** DELETE /api/v1/conversations/{id} — removes a conversation from history. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, JwtAuthenticationToken authentication) {
        String internalUserId = resolveInternalUserId(authentication);
        Conversation existing = getConversationUseCase.execute(EntityId.of(id));
        if (!existing.getUserId().equals(internalUserId)) {
            throw new ResourceNotFoundException("Conversation", id);
        }
        deleteConversationUseCase.execute(EntityId.of(id));
        return ResponseEntity.noContent().build();
    }

    private String resolveInternalUserId(JwtAuthenticationToken authentication) {
        String keycloakSubjectId = authentication.getToken().getSubject();
        return userRepository.findByKeycloakSubjectId(keycloakSubjectId)
                .map(u -> u.getId().value().toString())
                .orElseThrow(() -> new IllegalStateException("User not yet provisioned"));
    }
}
