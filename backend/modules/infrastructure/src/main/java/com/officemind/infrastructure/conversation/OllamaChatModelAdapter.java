package com.officemind.infrastructure.conversation;

import com.officemind.application.conversation.ChatModelPort;
import com.officemind.domain.conversation.Message;
import com.officemind.domain.conversation.MessageRole;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OllamaChatModelAdapter implements ChatModelPort {

    private final ChatModel chatModel;

    public OllamaChatModelAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateReply(List<Message> conversationHistory) {
        List<org.springframework.ai.chat.messages.Message> springAiMessages = conversationHistory.stream()
                .map(this::toSpringAiMessage)
                .toList();

        Prompt prompt = new Prompt(springAiMessages);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    private org.springframework.ai.chat.messages.Message toSpringAiMessage(Message message) {
        if (message.getRole() == MessageRole.USER) {
            return new UserMessage(message.getContent());
        }
        return new AssistantMessage(message.getContent());
    }
}
