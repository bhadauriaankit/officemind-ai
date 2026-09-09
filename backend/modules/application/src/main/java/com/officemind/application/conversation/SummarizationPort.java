package com.officemind.application.conversation;

import com.officemind.domain.conversation.Message;

import java.util.List;

/**
 * Port for condensing a list of messages into a short summary string.
 * Implemented in infrastructure by the Ollama adapter (Phase 10).
 *
 * The caller is responsible for merging any pre-existing summary with the
 * messages-to-summarize before passing them in, so this port stays simple.
 */
public interface SummarizationPort {

    /**
     * Summarizes the given messages into a concise paragraph.
     *
     * @param messages  the messages to condense -- caller should prepend
     *                  a synthetic "previous summary" entry when one exists
     * @return          a short summary string (typically 3-5 sentences)
     */
    String summarize(List<Message> messages);
}
