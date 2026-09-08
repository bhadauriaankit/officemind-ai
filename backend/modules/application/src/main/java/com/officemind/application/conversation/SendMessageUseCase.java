package com.officemind.application.conversation;

import com.officemind.application.agent.AgentRepositoryPort;
import com.officemind.application.documentchunk.SemanticSearchUseCase;
import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.agent.Agent;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.conversation.Message;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SendMessageUseCase {

    // Cosine similarity below this is treated as "not relevant enough to
    // bother the model with" -- avoids injecting noise into casual chat
    // that isn't actually about any uploaded document. Chosen empirically;
    // nomic-embed-text scores for genuinely relevant matches tend to sit
    // meaningfully above this in informal testing.
    private static final float RELEVANCE_THRESHOLD = 0.35f;
    private static final int RETRIEVAL_LIMIT = 3;

    private final ConversationRepositoryPort conversationRepository;
    private final ChatModelPort chatModelPort;
    private final SemanticSearchUseCase semanticSearchUseCase;
    private final AgentRepositoryPort agentRepository;

    public SendMessageUseCase(ConversationRepositoryPort conversationRepository,
                               ChatModelPort chatModelPort,
                               SemanticSearchUseCase semanticSearchUseCase,
                               AgentRepositoryPort agentRepository) {
        this.conversationRepository = conversationRepository;
        this.chatModelPort = chatModelPort;
        this.semanticSearchUseCase = semanticSearchUseCase;
        this.agentRepository = agentRepository;
    }

    /** Starts a brand new conversation, optionally with a persona Agent. */
    public Conversation startConversation(String userId, String userMessage, EntityId agentId) {
        Conversation conversation = Conversation.start(userId, userMessage, agentId);
        String context = retrieveContext(userMessage);
        String systemPromptOverride = resolveAgentPrompt(agentId);
        String reply = chatModelPort.generateReply(conversation.getMessages(), context, systemPromptOverride);
        conversation.appendMessage(Message.assistantMessage(reply));
        return conversationRepository.save(conversation);
    }

    /** Continues an existing conversation. The agent (if any) was chosen at
     *  start and is fixed for the conversation's lifetime, but its prompt
     *  content is still re-resolved live -- same pattern as AiSettings. */
    public Conversation continueConversation(EntityId conversationId, String userMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId));

        conversation.appendMessage(Message.userMessage(userMessage));
        String context = retrieveContext(userMessage);
        String systemPromptOverride = conversation.getAgentId().map(this::resolveAgentPrompt).orElse(null);
        String reply = chatModelPort.generateReply(conversation.getMessages(), context, systemPromptOverride);
        conversation.appendMessage(Message.assistantMessage(reply));

        return conversationRepository.save(conversation);
    }

    private String resolveAgentPrompt(EntityId agentId) {
        if (agentId == null) {
            return null;
        }
        // Agent may have been deleted since the conversation started
        // (ON DELETE SET NULL only clears the FK on the conversations row
        // itself, not any EntityId already loaded in memory here) --
        // fall back to the global prompt rather than error.
        return agentRepository.findById(agentId).map(Agent::getSystemPrompt).orElse(null);
    }

    private String retrieveContext(String userMessage) {
        List<SemanticSearchUseCase.Result> hits = semanticSearchUseCase.execute(userMessage, RETRIEVAL_LIMIT).stream()
                .filter(r -> r.score() >= RELEVANCE_THRESHOLD)
                .toList();

        if (hits.isEmpty()) {
            return null;
        }

        return IntStream.range(0, hits.size())
                .mapToObj(i -> {
                    SemanticSearchUseCase.Result r = hits.get(i);
                    return "[%d] (from \"%s\"): %s".formatted(i + 1, r.documentFileName(), r.content());
                })
                .collect(Collectors.joining("\n\n"));
    }
}
