package com.officemind.application.conversation;

import com.officemind.common.paging.PageResult;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.shared.EntityId;

import java.util.Optional;

public interface ConversationRepositoryPort {

    Conversation save(Conversation conversation);

    Optional<Conversation> findById(EntityId id);

    PageResult<Conversation> findAllByUserId(String userId, int page, int size);
}
