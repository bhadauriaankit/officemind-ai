package com.officemind.application.conversation;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteConversationUseCase {

    private final ConversationRepositoryPort conversationRepository;

    public DeleteConversationUseCase(ConversationRepositoryPort conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public void execute(EntityId conversationId) {
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId));
        conversationRepository.deleteById(conversationId);
    }
}
