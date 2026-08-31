package com.officemind.infrastructure.conversation;

import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.conversation.Message;
import com.officemind.domain.conversation.MessageRole;
import com.officemind.domain.shared.EntityId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class ConversationMapper {

    private ConversationMapper() {
    }

    static Conversation toDomain(ConversationJpaEntity entity) {
        List<Message> messages = entity.getMessages().stream()
                .map(m -> new Message(
                        MessageRole.valueOf(m.getRole().name()),
                        m.getContent(),
                        m.getSentAt()
                ))
                .toList();

        return Conversation.rehydrate(
                EntityId.of(entity.getId()),
                entity.getUserId().toString(),
                entity.getTitle(),
                messages,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    static ConversationJpaEntity toJpa(Conversation conversation) {
        ConversationJpaEntity entity = new ConversationJpaEntity(
                conversation.getId().value(),
                UUID.fromString(conversation.getUserId()),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
	        List<MessageJpaEntity> messageEntities = new ArrayList<>();
        List<Message> messages = conversation.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            MessageJpaEntity messageEntity = new MessageJpaEntity(
                    UUID.randomUUID(),
                    MessageJpaEntity.RoleJpa.valueOf(m.getRole().name()),
                    m.getContent(),
                    m.getSentAt(),
                    i
            );
            messageEntity.setConversation(entity);
            messageEntities.add(messageEntity);
        }
        entity.setMessages(messageEntities);


        return entity;
    }
}
