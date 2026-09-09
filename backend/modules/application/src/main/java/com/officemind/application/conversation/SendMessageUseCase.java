package com.officemind.application.conversation;

import com.officemind.application.agent.AgentRepositoryPort;
import com.officemind.application.documentchunk.SemanticSearchUseCase;
import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.agent.Agent;
import com.officemind.domain.conversation.Conversation;
import com.officemind.domain.conversation.Message;
import com.officemind.domain.shared.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SendMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendMessageUseCase.class);

    // Cosine similarity below this is treated as "not relevant enough to
    // bother the model with" -- avoids injecting noise into casual chat
    // that isn't actually about any uploaded document. Chosen empirically;
    // nomic-embed-text scores for genuinely relevant matches tend to sit
    // meaningfully above this in informal testing.
    private static final float RELEVANCE_THRESHOLD = 0.35f;
    private static final int RETRIEVAL_LIMIT = 3;

    // Phase 10: summarization thresholds.
    // SUMMARIZE_THRESHOLD: total message count at which we trigger pruning.
    // TAIL_KEEP: how many recent messages to keep verbatim after pruning.
    // Kept as named constants so they're easy to tune without touching logic.
    private static final int SUMMARIZE_THRESHOLD = 20;
    private static final int TAIL_KEEP = 6;

    private final ConversationRepositoryPort conversationRepository;
    private final ChatModelPort chatModelPort;
    private final SummarizationPort summarizationPort;
    private final SemanticSearchUseCase semanticSearchUseCase;
    private final AgentRepositoryPort agentRepository;

    public SendMessageUseCase(ConversationRepositoryPort conversationRepository,
                               ChatModelPort chatModelPort,
                               SummarizationPort summarizationPort,
                               SemanticSearchUseCase semanticSearchUseCase,
                               AgentRepositoryPort agentRepository) {
        this.conversationRepository = conversationRepository;
        this.chatModelPort = chatModelPort;
        this.summarizationPort = summarizationPort;
        this.semanticSearchUseCase = semanticSearchUseCase;
        this.agentRepository = agentRepository;
    }

    /** Starts a brand new conversation, optionally with a persona Agent. */
    public Conversation startConversation(String userId, String userMessage, EntityId agentId) {
        Conversation conversation = Conversation.start(userId, userMessage, agentId);
        String context = retrieveContext(userMessage);
        String systemPromptOverride = resolveAgentPrompt(agentId);
        String reply = chatModelPort.generateReply(conversation.getMessages(), context, systemPromptOverride,
                conversation.getSummary().orElse(null));
        conversation.appendMessage(Message.assistantMessage(reply));
        return conversationRepository.save(conversation);
    }

    /**
     * Continues an existing conversation. The agent (if any) was chosen at
     * start and is fixed for the conversation's lifetime, but its prompt
     * content is still re-resolved live -- same pattern as AiSettings.
     *
     * Phase 10: after appending the assistant reply, checks whether the
     * conversation has grown past SUMMARIZE_THRESHOLD and, if so, summarizes
     * older messages and prunes them from the DB-persisted list.
     */
    public Conversation continueConversation(EntityId conversationId, String userMessage) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId));

        conversation.appendMessage(Message.userMessage(userMessage));
        String context = retrieveContext(userMessage);
        String systemPromptOverride = conversation.getAgentId().map(this::resolveAgentPrompt).orElse(null);
        String reply = chatModelPort.generateReply(conversation.getMessages(), context, systemPromptOverride,
                conversation.getSummary().orElse(null));
        conversation.appendMessage(Message.assistantMessage(reply));

        // Phase 10: prune + summarize if we have crossed the threshold.
        maybeCondenseHistory(conversation);

        return conversationRepository.save(conversation);
    }

    /**
     * If the conversation has grown past SUMMARIZE_THRESHOLD, prunes older
     * messages and replaces them with a condensed summary stored on the
     * Conversation aggregate. The summary is then injected into every
     * subsequent LLM call via ChatModelPort so the model retains context.
     */
    private void maybeCondenseHistory(Conversation conversation) {
        if (conversation.getMessages().size() < SUMMARIZE_THRESHOLD) {
            return;
        }

        log.info("Conversation {} has {} messages -- condensing history (keeping tail of {})",
                conversation.getId().value(), conversation.getMessages().size(), TAIL_KEEP);

        // Build the input for the summarizer: if a previous summary exists,
        // prepend it as a synthetic assistant message so the new summary
        // incorporates earlier context too (rolling window effect).
        List<Message> toSummarize = new ArrayList<>();
        conversation.getSummary().ifPresent(prev ->
                toSummarize.add(Message.assistantMessage("[Earlier conversation summary]: " + prev)));

        List<Message> pruned = conversation.pruneMessagesOlderThan(TAIL_KEEP);
        toSummarize.addAll(pruned);

        String newSummary = summarizationPort.summarize(toSummarize);
        conversation.setSummary(newSummary);

        log.info("Conversation {} condensed: {} messages pruned, summary updated.",
                conversation.getId().value(), pruned.size());
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
