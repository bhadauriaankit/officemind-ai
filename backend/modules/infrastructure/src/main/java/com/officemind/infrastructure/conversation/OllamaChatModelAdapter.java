package com.officemind.infrastructure.conversation;

import com.officemind.application.aisettings.AiSettingsRepositoryPort;
import com.officemind.application.conversation.ChatModelPort;
import com.officemind.domain.aisettings.AiSettings;
import com.officemind.domain.conversation.Message;
import com.officemind.domain.conversation.MessageRole;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OllamaChatModelAdapter implements ChatModelPort {

    private final ChatModel chatModel;
    private final AiSettingsRepositoryPort aiSettingsRepository;

    public OllamaChatModelAdapter(ChatModel chatModel, AiSettingsRepositoryPort aiSettingsRepository) {
        this.chatModel = chatModel;
        this.aiSettingsRepository = aiSettingsRepository;
    }

    @Override
    public String generateReply(List<Message> conversationHistory) {
        AiSettings settings = aiSettingsRepository.get();

        List<org.springframework.ai.chat.messages.Message> springAiMessages = new ArrayList<>();
        if (settings.getSystemPrompt() != null && !settings.getSystemPrompt().isBlank()) {
            springAiMessages.add(new SystemMessage(settings.getSystemPrompt()));
        }
        conversationHistory.stream().map(this::toSpringAiMessage).forEach(springAiMessages::add);

        // spring-ai 1.0.0-M1's OllamaOptions predates the .builder() API introduced
        // in later milestones — it uses the fluent withX(...) pattern instead, and
        // temperature is a Float, not our domain's double, hence the explicit cast.
        OllamaOptions options = OllamaOptions.create()
                .withModel(settings.getModelName())
                .withTemperature((float) settings.getTemperature());

        Prompt prompt = new Prompt(springAiMessages, options);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    private org.springframework.ai.chat.messages.Message toSpringAiMessage(Message message) {
        if (message.getRole() == MessageRole.USER) {
            return new UserMessage(message.getContent());
        }
        return new AssistantMessage(message.getContent());
    }
}
