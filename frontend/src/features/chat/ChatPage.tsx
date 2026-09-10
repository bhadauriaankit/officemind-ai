import { FormEvent, useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import {
  useConversationList,
  useConversation,
  useStartConversation,
  useSendMessage,
  useDeleteConversation,
  extractErrorMessage,
  ChatMessage,
} from "./useConversation";
import { useAgents } from "@/features/agents/useAgents";
import { NotificationBell } from "@/features/notifications/NotificationBell";

export function ChatPage() {
  const auth = useAuth();
  const [activeId, setActiveId] = useState<string | null>(null);
  const [selectedAgentId, setSelectedAgentId] = useState<string>("");
  const [input, setInput] = useState("");
  const [deletingId, setDeletingId] = useState<string | null>(null);
  const bottomRef = useRef<HTMLDivElement>(null);

  const { data: conversations } = useConversationList();
  const { data: activeConversation } = useConversation(activeId);
  const { data: agents } = useAgents();
  const startConversation = useStartConversation();
  const sendMessage = useSendMessage();
  const deleteConversation = useDeleteConversation();

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
  }, [input]);

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

  function handleDelete(conversationId: string) {
    setDeletingId(conversationId);
  }

  function confirmDelete() {
    if (!deletingId) return;
    deleteConversation.mutate(deletingId, {
      onSuccess: () => {
        if (activeId === deletingId) setActiveId(null);
        setDeletingId(null);
      },
      onError: () => setDeletingId(null),
    });
  }

  // FIX: signoutRedirect() ends the Keycloak SSO session — removeUser() only
  // cleared local state which caused silent re-login on page refresh.
  function handleLogout() {
    auth.signoutRedirect();
  }

  const userName = auth.user?.profile?.name as string | undefined;
  const userInitials = userName?.split(" ").map((w) => w[0]).join("").slice(0, 2).toUpperCase() ?? "?";

  return (
    <div className="flex h-screen bg-slate-50 font-sans overflow-hidden">

      {/* Delete Confirmation Modal */}
      {deletingId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
          <div className="w-full max-w-sm rounded-2xl bg-white shadow-2xl border border-slate-200 p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
                <svg className="w-5 h-5 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              </div>
              <div>
                <h3 className="text-base font-bold text-slate-900">Delete Conversation</h3>
                <p className="text-xs text-slate-500">This action cannot be undone.</p>
              </div>
            </div>
            <p className="text-sm text-slate-600 mb-5">Are you sure you want to permanently delete this conversation and all its messages?</p>
            <div className="flex gap-3">
              <button onClick={() => setDeletingId(null)} className="flex-1 rounded-xl border border-slate-200 px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors">Cancel</button>
              <button onClick={confirmDelete} disabled={deleteConversation.isPending} className="flex-1 rounded-xl bg-red-600 px-4 py-2.5 text-sm font-bold text-white hover:bg-red-700 disabled:opacity-50 transition-colors">
                {deleteConversation.isPending ? "Deleting…" : "Delete"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── Sidebar ── */}
      <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col shrink-0 z-20">
        <div className="p-5 border-b border-white/10">
          <Link to="/" className="flex items-center gap-2.5 text-white font-bold text-lg hover:text-blue-400 transition-colors group">
            <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center shadow-md">
              <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
            </div>
            OfficeMind AI
          </Link>
        </div>

        <div className="p-4">
          <button
            onClick={startNewChat}
            className="w-full flex items-center justify-center gap-2 rounded-xl bg-blue-600 px-4 py-3 text-sm font-bold text-white shadow-md hover:bg-blue-500 transition-all"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 4v16m8-8H4" /></svg>
            New Conversation
          </button>
        </div>

        {/* Nav links */}
        <div className="px-4 pb-2">
          {[
            { to: "/search", icon: "M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z", label: "Search" },
            { to: "/admin", icon: "M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065zM15 12a3 3 0 11-6 0 3 3 0 016 0z", label: "Admin" },
          ].map((item) => (
            <Link key={item.to} to={item.to} className="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm text-slate-400 hover:bg-slate-800 hover:text-white transition-all mb-1">
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={item.icon} /></svg>
              {item.label}
            </Link>
          ))}
        </div>

        {/* Conversation list */}
        <div className="flex-1 overflow-y-auto px-4 custom-scrollbar">
          {conversations && conversations.length > 0 && (
            <p className="px-2 py-2 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Recent Chats</p>
          )}
          {conversations?.map((c) => (
            <div
              key={c.id}
              className={`group relative flex items-center rounded-xl mb-1 transition-all ${
                c.id === activeId ? "bg-slate-800 text-white" : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
              }`}
            >
              <button onClick={() => setActiveId(c.id)} className="flex-1 flex items-center gap-2.5 px-3 py-2.5 text-left text-sm truncate min-w-0">
                <svg className={`w-3.5 h-3.5 shrink-0 ${c.id === activeId ? "text-blue-400" : "text-slate-600"}`} fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" /></svg>
                <span className="truncate text-xs leading-tight">{c.title || "New conversation"}</span>
              </button>
              {/* Delete button — visible on hover */}
              <button
                onClick={(e) => { e.stopPropagation(); handleDelete(c.id); }}
                className="shrink-0 mr-2 opacity-0 group-hover:opacity-100 p-1 rounded-lg text-slate-500 hover:text-red-400 hover:bg-red-900/30 transition-all"
                title="Delete conversation"
              >
                <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              </button>
            </div>
          ))}
        </div>

        {/* User profile + logout */}
        <div className="p-4 border-t border-white/10">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center text-xs font-bold text-white shrink-0">
              {userInitials}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold text-slate-200 truncate">{userName ?? "User"}</p>
              <p className="text-[10px] text-slate-500 truncate">Employee</p>
            </div>
            <button
              onClick={handleLogout}
              title="Sign out"
              className="shrink-0 rounded-lg p-1.5 text-slate-500 hover:bg-slate-800 hover:text-red-400 transition-all"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
            </button>
          </div>
        </div>
      </aside>

      {/* ── Main ── */}
      <div className="flex flex-1 flex-col relative bg-slate-50/50 overflow-hidden">
        <header className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-200 bg-white/90 backdrop-blur-md px-8 py-3.5 shadow-sm">
          <div>
            <h2 className="text-lg font-bold text-slate-800 tracking-tight">
              {activeConversation ? activeConversation.title || "Conversation" : "New Conversation"}
            </h2>
            {activeAgentName && (
              <p className="text-xs font-bold text-blue-600 flex items-center gap-1.5 uppercase tracking-wide">
                <span className="w-1.5 h-1.5 rounded-full bg-blue-500 animate-pulse"></span>
                {activeAgentName}
              </p>
            )}
          </div>
          <div className="flex items-center gap-3">
            <Link to="/search" className="text-sm font-semibold text-slate-600 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 px-3 py-1.5 rounded-xl transition-colors">Search</Link>
            <NotificationBell />
          </div>
        </header>

        <div className="flex-1 overflow-y-auto px-6 py-8 custom-scrollbar">
          {!activeConversation && (
            <div className="absolute inset-0 flex flex-col items-center justify-center p-8 text-center">
              <div className="w-20 h-20 rounded-3xl bg-blue-50 border-2 border-blue-100 flex items-center justify-center mb-6">
                <svg className="w-10 h-10 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" /></svg>
              </div>
              <h3 className="text-2xl font-extrabold text-slate-900 mb-2">Start a conversation</h3>
              <p className="text-slate-500 max-w-sm text-base mb-8">Ask questions, analyze documents, or get help with your work tasks.</p>
              {agents && agents.length > 0 && (
                <div className="w-full max-w-xs bg-white rounded-2xl border border-slate-200 shadow-sm p-4">
                  <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-2">Select AI Agent</label>
                  <div className="relative">
                    <select
                      value={selectedAgentId}
                      onChange={(e) => setSelectedAgentId(e.target.value)}
                      className="w-full appearance-none rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 font-medium focus:border-blue-500 focus:bg-white focus:outline-none transition-all cursor-pointer"
                    >
                      <option value="">General Assistant</option>
                      {agents.filter((a) => a.key !== "general").map((a) => (
                        <option key={a.id} value={a.id}>{a.name}</option>
                      ))}
                    </select>
                    <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-slate-400">
                      <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M19 9l-7 7-7-7" /></svg>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          <div className="mx-auto max-w-3xl space-y-6 pb-4">
            {activeConversation?.messages.map((m, i) => <MessageBubble key={i} message={m} />)}
            {isSending && (
              <div className="flex justify-start">
                <div className="flex gap-3 max-w-[85%]">
                  <div className="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center shrink-0">
                    <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
                  </div>
                  <div className="rounded-2xl rounded-tl-none bg-white border border-slate-200 px-5 py-4 shadow-sm flex items-center gap-1.5">
                    <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce [animation-delay:0ms]"></span>
                    <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce [animation-delay:100ms]"></span>
                    <span className="w-2 h-2 bg-slate-400 rounded-full animate-bounce [animation-delay:200ms]"></span>
                  </div>
                </div>
              </div>
            )}
            <div ref={bottomRef} />
          </div>
        </div>

        {/* Input bar */}
        <div className="bg-white border-t border-slate-200 px-6 py-4">
          {activeError && (
            <div className="mb-3 mx-auto max-w-3xl flex items-center justify-between rounded-xl border border-red-200 bg-red-50 px-4 py-2.5 text-sm text-red-700">
              <span className="font-medium">{extractErrorMessage(activeError)}</span>
              <button onClick={() => { startConversation.reset(); sendMessage.reset(); }} className="text-red-500 hover:text-red-800 p-1 rounded-lg transition-colors">✕</button>
            </div>
          )}
          <form onSubmit={handleSubmit} className="mx-auto max-w-3xl flex gap-3">
            <input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask a question or request a task…"
              disabled={isSending}
              className="flex-1 rounded-xl border border-slate-200 bg-slate-50 px-5 py-3 text-sm text-slate-800 placeholder-slate-400 focus:border-blue-500 focus:bg-white focus:outline-none focus:ring-4 focus:ring-blue-500/10 disabled:opacity-60 transition-all"
            />
            <button
              type="submit"
              disabled={isSending || !input.trim()}
              className="flex items-center justify-center w-11 h-11 rounded-xl bg-blue-600 text-white disabled:bg-slate-100 disabled:text-slate-300 hover:bg-blue-700 shadow-sm transition-all shrink-0"
            >
              <svg className="w-5 h-5 ml-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" /></svg>
            </button>
          </form>
          <p className="text-center mt-2 text-xs text-slate-400">OfficeMind AI may occasionally make mistakes. Verify important information.</p>
        </div>
      </div>
    </div>
  );
}

function MessageBubble({ message }: { message: ChatMessage }) {
  const isUser = message.role === "USER";
  return (
    <div className={`flex ${isUser ? "justify-end" : "justify-start"}`}>
      <div className={`flex gap-3 max-w-[85%] ${isUser ? "flex-row-reverse" : "flex-row"}`}>
        <div className={`w-9 h-9 rounded-xl flex items-center justify-center shrink-0 text-xs font-bold shadow-sm ${isUser ? "bg-slate-100 text-slate-600" : "bg-blue-600 text-white"}`}>
          {isUser ? "ME" : (
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
          )}
        </div>
        <div className={`rounded-2xl px-5 py-3.5 text-sm leading-relaxed shadow-sm ${isUser ? "bg-slate-800 text-white rounded-tr-sm" : "bg-white border border-slate-200 text-slate-800 rounded-tl-sm"}`}>
          <div className="whitespace-pre-wrap">{message.content}</div>
        </div>
      </div>
    </div>
  );
}
