package com.officemind.application.conversation;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.conversation.Message;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class SendMessageUseCase {

    private final ConversationRepositoryPort conversationRepository;
    private final ChatModelPort chatModelPort;

    public SendMessageUseCase(ConversationRepositoryPort conversationRepository, ChatModelPort chatModelPort) {
        this.conversationRepository = conversationRepository;
        this.chatModelPort = chatModelPort;
    }

    /** Starts a brand new conversation with the given first message. */
    public Conversation startConversation(String userId, String userMessage) {
        Conversation conversation = Conversation.start(userId, userMessage);
        String reply = chatModelPort.generateReply(conversation.getMessages());
        conversation.appendMessage(Message.assistantMessage(reply));
        return conversationRepository.save(conversation);
    }

    /** Continues an existing conversation. */
    public Conversation continueConversation(EntityId conversationId, String userMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId));

        conversation.appendMessage(Message.userMessage(userMessage));
        String reply = chatModelPort.generateReply(conversation.getMessages());
        conversation.appendMessage(Message.assistantMessage(reply));

        return conversationRepository.save(conversation);
    }
}
