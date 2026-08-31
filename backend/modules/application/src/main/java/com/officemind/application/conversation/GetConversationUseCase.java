package com.officemind.application.conversation;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class GetConversationUseCase {

    private final ConversationRepositoryPort conversationRepository;

    public GetConversationUseCase(ConversationRepositoryPort conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation execute(EntityId id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", id));
    }
}
