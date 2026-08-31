package com.officemind.application.conversation;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.conversation.Conversation;
import org.springframework.stereotype.Service;

@Service
public class ListConversationsUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final ConversationRepositoryPort conversationRepository;

    public ListConversationsUseCase(ConversationRepositoryPort conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public PageResult<Conversation> execute(String userId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return conversationRepository.findAllByUserId(userId, safePage, safeSize);
    }
}
