import { FormEvent, useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import {
  useConversationList,
  useConversation,
  useStartConversation,
  useSendMessage,
  extractErrorMessage,
  ChatMessage,
} from "./useConversation";
import { useAgents } from "@/features/agents/useAgents";
import { NotificationBell } from "@/features/notifications/NotificationBell";

export function ChatPage() {
  const [activeId, setActiveId] = useState<string | null>(null);
  const [selectedAgentId, setSelectedAgentId] = useState<string>("");
  const [input, setInput] = useState("");
  const bottomRef = useRef<HTMLDivElement>(null);

  const { data: conversations } = useConversationList();
  const { data: activeConversation } = useConversation(activeId);
  const { data: agents } = useAgents();
  const startConversation = useStartConversation();
  const sendMessage = useSendMessage();

  const isSending = startConversation.isPending || sendMessage.isPending;
  const activeError = startConversation.error || sendMessage.error;

  const activeAgentName = activeConversation?.agentId
    ? agents?.find((a) => a.id === activeConversation.agentId)?.name
    : null;

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [activeConversation?.messages.length, isSending]);

  useEffect(() => {
    if (input && (startConversation.isError || sendMessage.isError)) {
      startConversation.reset();
      sendMessage.reset();
    }
  }, [input, startConversation, sendMessage]);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const message = input.trim();
    if (!message || isSending) return;
    setInput("");

    if (!activeId) {
      startConversation.mutate(
        { message, agentId: selectedAgentId || null },
        { onSuccess: (conversation) => setActiveId(conversation.id) }
      );
    } else {
      sendMessage.mutate({ conversationId: activeId, message });
    }
  }

  function startNewChat() {
    setActiveId(null);
    setSelectedAgentId("");
  }

  return (
    <div className="flex h-screen bg-slate-50 font-sans overflow-hidden">
      {/* Sidebar */}
      <aside className="w-72 bg-slate-900 text-slate-300 flex flex-col shadow-2xl z-20 shrink-0">
        <div className="p-5 border-b border-white/10 flex items-center justify-between">
          <Link to="/" className="text-white font-bold text-xl flex items-center gap-3 hover:text-blue-400 transition-colors group">
            <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg flex items-center justify-center shadow-lg group-hover:shadow-blue-500/20 transition-all">
              <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            OfficeMind
          </Link>
        </div>
        
        <div className="p-5">
          <button
            onClick={startNewChat}
            className="w-full flex items-center justify-center gap-2 rounded-xl bg-blue-600 px-4 py-3.5 text-sm font-bold text-white shadow-lg shadow-blue-900/20 hover:bg-blue-500 transition-all active:scale-95"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" /></svg>
            New Conversation
          </button>
        </div>

        <div className="flex-1 overflow-y-auto px-4 py-2 space-y-1 custom-scrollbar">
          <div className="px-2 py-3 text-xs font-bold text-slate-500 uppercase tracking-wider">Recent History</div>
          {conversations?.map((c) => (
            <button
              key={c.id}
              onClick={() => setActiveId(c.id)}
              className={`group flex items-center w-full rounded-xl px-3 py-3 text-left text-sm transition-all ${
                c.id === activeId
                  ? "bg-slate-800 text-white font-semibold shadow-inner"
                  : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
              }`}
            >
              <svg className={`w-4 h-4 shrink-0 mr-3 transition-colors ${c.id === activeId ? "text-blue-400" : "text-slate-600 group-hover:text-slate-400"}`} fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" /></svg>
              <span className="truncate leading-tight">{c.title || "New conversation"}</span>
            </button>
          ))}
        </div>
        
        <div className="p-4 border-t border-white/10 bg-slate-900">
          <Link to="/admin" className="flex items-center gap-3 text-sm font-medium text-slate-400 hover:text-white hover:bg-slate-800 p-3 rounded-xl transition-all">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
            Admin Panel
          </Link>
        </div>
      </aside>

      {/* Main chat area */}
      <div className="flex flex-1 flex-col relative bg-slate-50/50">
        <header className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-200 bg-white/90 backdrop-blur-md px-8 py-4 shadow-sm">
          <div>
            <h2 className="text-xl font-bold text-slate-800 tracking-tight">
              {activeConversation ? activeConversation.title || "Conversation" : "New Conversation"}
            </h2>
            {activeAgentName && (
              <p className="mt-1 text-xs font-bold text-blue-600 flex items-center gap-1.5 uppercase tracking-wide">
                <span className="w-2 h-2 rounded-full bg-blue-500 animate-pulse"></span>
                {activeAgentName}
              </p>
            )}
          </div>
          <div className="flex items-center gap-4">
            <Link to="/search" className="text-sm font-bold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 px-4 py-2 rounded-xl transition-colors shadow-sm">Global Search</Link>
            <div className="w-px h-6 bg-slate-300"></div>
            <NotificationBell />
          </div>
        </header>

        <div className="flex-1 overflow-y-auto px-4 py-8 custom-scrollbar relative">
          {!activeConversation && (
            <div className="absolute inset-0 flex flex-col items-center justify-center p-6 text-center">
              <div className="w-24 h-24 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-[2rem] flex items-center justify-center mb-8 shadow-inner transform -rotate-3 transition-transform hover:rotate-0 duration-300">
                <svg className="w-12 h-12 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" /></svg>
              </div>
              <h3 className="text-3xl font-extrabold text-slate-900 mb-3 tracking-tight">How can I help you?</h3>
              <p className="text-slate-500 max-w-md mb-10 text-lg">Ask questions about company policies, analyze documents, or get help with your daily tasks.</p>
              
              {agents && agents.length > 0 && (
                <div className="w-full max-w-sm bg-white p-6 rounded-3xl shadow-xl shadow-slate-200/50 border border-slate-100">
                  <label className="mb-3 block text-sm font-bold text-slate-700 text-left uppercase tracking-wider">
                    Select AI Agent
                  </label>
                  <div className="relative">
                    <select
                      value={selectedAgentId}
                      onChange={(e) => setSelectedAgentId(e.target.value)}
                      className="w-full appearance-none rounded-xl border border-slate-200 bg-slate-50 px-5 py-3.5 text-sm text-slate-800 font-semibold focus:border-blue-500 focus:bg-white focus:outline-none focus:ring-4 focus:ring-blue-500/10 transition-all cursor-pointer shadow-sm"
                    >
                      <option value="">General Assistant (Default)</option>
                      {agents
                        .filter((a) => a.key !== "general")
                        .map((a) => (
                          <option key={a.id} value={a.id}>
                            {a.name}
                          </option>
                        ))}
                    </select>
                    <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-slate-400">
                      <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M19 9l-7 7-7-7" /></svg>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
          <div className="mx-auto max-w-4xl space-y-8 pb-10">
            {activeConversation?.messages.map((m, i) => (
              <MessageBubble key={i} message={m} />
            ))}
            {isSending && (
              <div className="flex justify-start">
                <div className="flex gap-4 max-w-[85%]">
                  <div className="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center shrink-0 shadow-sm border border-blue-200">
                    <span className="w-3 h-3 bg-blue-600 rounded-full animate-ping"></span>
                  </div>
                  <div className="rounded-2xl rounded-tl-none bg-white border border-slate-200 px-6 py-4 text-sm text-slate-500 shadow-sm flex items-center gap-2">
                    <span className="font-medium text-slate-600 mr-2">OfficeMind is thinking</span>
                    <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce"></span>
                    <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></span>
                    <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></span>
                  </div>
                </div>
              </div>
            )}
            <div ref={bottomRef} className="h-4" />
          </div>
        </div>

        <div className="bg-gradient-to-t from-white via-white to-transparent pt-6 pb-6 px-6">
          {activeError && (
            <div className="mx-auto mb-4 w-full max-w-4xl">
              <div className="flex items-center justify-between rounded-xl border border-red-200 bg-red-50 px-5 py-3 text-sm text-red-700 shadow-sm">
                <span className="flex items-center gap-2 font-medium">
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                  {extractErrorMessage(activeError)}
                </span>
                <button
                  onClick={() => {
                    startConversation.reset();
                    sendMessage.reset();
                  }}
                  className="text-red-500 hover:text-red-800 p-1.5 hover:bg-red-100 rounded-lg transition-colors"
                  aria-label="Dismiss error"
                >
                  ✕
                </button>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="mx-auto max-w-4xl relative shadow-[0_8px_30px_rgb(0,0,0,0.08)] rounded-2xl group">
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask a question or request a task..."
              disabled={isSending}
              className="w-full rounded-2xl border-2 border-slate-100 bg-white px-6 py-4 pr-16 text-slate-800 focus:border-blue-500 focus:outline-none focus:ring-4 focus:ring-blue-500/10 disabled:opacity-60 transition-all text-base placeholder-slate-400"
            />
            <button
              type="submit"
              disabled={isSending || !input.trim()}
              className="absolute right-2 top-2 bottom-2 aspect-square flex items-center justify-center rounded-xl bg-blue-600 text-white disabled:bg-slate-100 disabled:text-slate-300 hover:bg-blue-700 shadow-sm transition-all"
            >
              <svg className="w-5 h-5 ml-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
            </button>
          </form>
          <div className="text-center mt-4">
            <span className="text-xs font-medium text-slate-400">OfficeMind AI is an AI assistant and may occasionally generate incorrect information.</span>
          </div>
        </div>
      </div>
    </div>
  );
}

function MessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === "USER";
  return (
    <div className={`flex w-full ${isUser ? "justify-end" : "justify-start"}`}>
      <div className={`flex gap-4 max-w-[85%] ${isUser ? "flex-row-reverse" : "flex-row"}`}>
        
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 shadow-sm border ${isUser ? "bg-slate-100 border-slate-200" : "bg-gradient-to-br from-blue-500 to-blue-700 border-blue-800"}`}>
          {isUser ? (
            <span className="text-sm font-bold text-slate-600">ME</span>
          ) : (
            <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
          )}
        </div>

        <div
          className={`rounded-3xl px-6 py-4 text-[15px] leading-relaxed shadow-sm ${
            isUser 
              ? "bg-slate-800 text-white rounded-tr-sm" 
              : "bg-white border border-slate-200 text-slate-800 rounded-tl-sm"
          }`}
        >
          <div className="whitespace-pre-wrap">{message.content}</div>
        </div>
        
      </div>
    </div>
  );
}
