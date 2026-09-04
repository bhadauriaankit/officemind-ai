 import { FormEvent, useEffect, useRef, useState } from "react";
import {
  useConversationList,
  useConversation,
  useStartConversation,
  useSendMessage,
  extractErrorMessage,
  ChatMessage,
} from "./useConversation";

export function ChatPage() {
  const [activeId, setActiveId] = useState<string | null>(null);
  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);

  const { data: conversations } = useConversationList();
  const { data: activeConversation } = useConversation(activeId);
  const startConversation = useStartConversation();
  const sendMessage = useSendMessage();

  const isSending = startConversation.isPending || sendMessage.isPending;
  const activeError = startConversation.error || sendMessage.error;

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [activeConversation?.messages.length]);

  // Clear a stale error as soon as the user starts typing again.
  useEffect(() => {
    if (input && (startConversation.isError || sendMessage.isError)) {
      startConversation.reset();
      sendMessage.reset();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [input]);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const message = input.trim();
    if (!message || isSending) return;
    setInput("");

    if (!activeId) {
      startConversation.mutate(message, {
        onSuccess: (conversation) => setActiveId(conversation.id),
      });
    } else {
      sendMessage.mutate({ conversationId: activeId, message });
    }
  }

  return (
    <div className="flex h-screen bg-slate-50">
      {/* Sidebar: conversation list */}
      <aside className="w-64 border-r border-slate-200 bg-white p-4">
        <button
          onClick={() => setActiveId(null)}
          className="mb-4 w-full rounded-md bg-slate-900 px-3 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          + New chat
        </button>
        <div className="space-y-1">
          {conversations?.map((c) => (
            <button
              key={c.id}
              onClick={() => setActiveId(c.id)}
              className={`block w-full truncate rounded-md px-3 py-2 text-left text-sm ${
                c.id === activeId
                  ? "bg-slate-100 font-medium text-slate-900"
                  : "text-slate-600 hover:bg-slate-50"
              }`}
            >
              {c.title || "New conversation"}
            </button>
          ))}
        </div>
      </aside>

      {/* Main chat area */}
      <div className="flex flex-1 flex-col">
        <header className="border-b border-slate-200 bg-white px-6 py-4">
          <h1 className="text-lg font-bold text-slate-900">OfficeMind AI Assistant</h1>
        </header>

        <div className="flex-1 overflow-y-auto px-6 py-6">
          {!activeConversation && (
            <div className="flex h-full items-center justify-center text-slate-400">
              Ask me anything to get started.
            </div>
          )}
          <div className="mx-auto max-w-2xl space-y-4">
            {activeConversation?.messages.map((m, i) => (
              <MessageBubble key={i} message={m} />
            ))}
            {isSending && (
              <div className="flex justify-start">
                <div className="rounded-lg bg-slate-100 px-4 py-2 text-sm text-slate-500">
                  Thinking…
                </div>
              </div>
            )}
            <div ref={bottomRef} />
          </div>
        </div>

        {activeError && (
          <div className="mx-auto mb-2 w-full max-w-2xl px-6">
            <div className="flex items-center justify-between rounded-md border border-red-200 bg-red-50 px-4 py-2 text-sm text-red-700">
              <span>{extractErrorMessage(activeError)}</span>
              <button
                onClick={() => {
                  startConversation.reset();
                  sendMessage.reset();
                }}
                className="ml-4 text-red-500 hover:text-red-700"
                aria-label="Dismiss error"
              >
                ✕
              </button>
            </div>
          </div>
        )}

        <form onSubmit={handleSubmit} className="border-t border-slate-200 bg-white p-4">
          <div className="mx-auto flex max-w-2xl gap-2">
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Type a message…"
              disabled={isSending}
              className="flex-1 rounded-md border border-slate-300 px-4 py-2 text-sm disabled:opacity-50"
            />
            <button
              type="submit"
              disabled={isSending || !input.trim()}
              className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
            >
              Send
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function MessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === "USER";
  return (
    <div className={`flex ${isUser ? "justify-end" : "justify-start"}`}>
      <div
        className={`max-w-lg rounded-lg px-4 py-2 text-sm ${
          isUser ? "bg-slate-900 text-white" : "bg-slate-100 text-slate-800"
        }`}
      >
        {message.content}
      </div>
    </div>
  );
}
