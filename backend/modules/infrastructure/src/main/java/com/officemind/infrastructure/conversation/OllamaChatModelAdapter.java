package com.officemind.infrastructure.conversation;

import com.officemind.application.aisettings.AiSettingsRepositoryPort;
import com.officemind.application.conversation.ChatModelPort;
import com.officemind.application.conversation.SummarizationPort;
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
import java.util.stream.Collectors;

/**
 * Adapter implementing both ChatModelPort (conversation replies) and
 * SummarizationPort (condensing older messages -- Phase 10) against the
 * same Ollama ChatModel bean. Both use the live AiSettings model and
 * temperature so admin config changes take effect immediately.
 */
@Component
public class OllamaChatModelAdapter implements ChatModelPort, SummarizationPort {

    private final ChatModel chatModel;
    private final AiSettingsRepositoryPort aiSettingsRepository;

    public OllamaChatModelAdapter(ChatModel chatModel, AiSettingsRepositoryPort aiSettingsRepository) {
        this.chatModel = chatModel;
        this.aiSettingsRepository = aiSettingsRepository;
    }

    @Override
    public String generateReply(List<Message> conversationHistory,
                                String retrievedContext,
                                String systemPromptOverride,
                                String conversationSummary) {
        AiSettings settings = aiSettingsRepository.get();

        List<org.springframework.ai.chat.messages.Message> springAiMessages = new ArrayList<>();

        // Phase 7: a selected persona Agent's prompt takes priority over
        // the global AiSettings prompt; falls back to the global one for
        // agent-less conversations (unchanged pre-Phase-7 behavior).
        String effectiveSystemPrompt = (systemPromptOverride != null && !systemPromptOverride.isBlank())
                ? systemPromptOverride
                : settings.getSystemPrompt();
        if (effectiveSystemPrompt != null && !effectiveSystemPrompt.isBlank()) {
            springAiMessages.add(new SystemMessage(effectiveSystemPrompt));
        }

        // Phase 10: inject the rolling summary of pruned messages so the
        // model retains earlier context without seeing the full history.
        if (conversationSummary != null && !conversationSummary.isBlank()) {
            springAiMessages.add(new SystemMessage(
                    "Summary of earlier conversation (before recent messages below):\n"
                    + conversationSummary));
        }

        if (retrievedContext != null && !retrievedContext.isBlank()) {
            springAiMessages.add(new SystemMessage(
                    "The following excerpts from company documents may be relevant to the "
                    + "user's question. Use them if helpful, and mention which document you "
                    + "drew from. If they are not relevant, ignore them and answer normally.\n\n"
                    + retrievedContext));
        }
        conversationHistory.stream().map(this::toSpringAiMessage).forEach(springAiMessages::add);

        // spring-ai 1.0.0-M1's OllamaOptions predates the .builder() API introduced
        // in later milestones -- it uses the fluent withX(...) pattern instead, and
        // temperature is a Float, not our domain's double, hence the explicit cast.
        OllamaOptions options = OllamaOptions.create()
                .withModel(settings.getModelName())
                .withTemperature((float) settings.getTemperature());

        Prompt prompt = new Prompt(springAiMessages, options);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    /**
     * Phase 10: summarizes a list of messages into a short paragraph.
     * Uses the same Ollama model configured in AiSettings.
     *
     * Intentionally uses a fixed, low temperature so summaries are
     * factual and stable regardless of the admin-configured setting.
     */
    @Override
    public String summarize(List<Message> messages) {
        AiSettings settings = aiSettingsRepository.get();

        String transcript = messages.stream()
                .map(m -> m.getRole().name() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        List<org.springframework.ai.chat.messages.Message> springAiMessages = new ArrayList<>();
        springAiMessages.add(new SystemMessage(
                "You are a precise summarizer. Summarize the following conversation excerpt "
                + "in 3 to 5 concise sentences. Preserve all key facts, decisions, questions "
                + "asked, and answers given. Do not add commentary or opinions."));
        springAiMessages.add(new UserMessage(transcript));

        // Use a fixed low temperature for summaries -- factual, not creative.
        OllamaOptions options = OllamaOptions.create()
                .withModel(settings.getModelName())
                .withTemperature(0.1f);

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
