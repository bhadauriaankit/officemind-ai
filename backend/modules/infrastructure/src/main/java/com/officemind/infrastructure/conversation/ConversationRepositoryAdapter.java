package com.officemind.infrastructure.conversation;

import com.officemind.application.conversation.ConversationRepositoryPort;
import com.officemind.common.paging.PageResult;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.shared.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ConversationRepositoryAdapter implements ConversationRepositoryPort {

    private final ConversationJpaRepository jpaRepository;

    public ConversationRepositoryAdapter(ConversationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Conversation save(Conversation conversation) {
        ConversationJpaEntity saved = jpaRepository.save(ConversationMapper.toJpa(conversation));
        return ConversationMapper.toDomain(saved);
    }

    @Override
    public Optional<Conversation> findById(EntityId id) {
        return jpaRepository.findById(id.value()).map(ConversationMapper::toDomain);
    }

    @Override
    public PageResult<Conversation> findAllByUserId(String userId, int page, int size) {
        Page<ConversationJpaEntity> result = jpaRepository.findAllByUserId(
                UUID.fromString(userId), PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(ConversationMapper::toDomain).toList(),
                page,
                size,
                result.getTotalElements()
        );
    }
}
