import { FormEvent, useState } from "react";
import { useAgents, Agent } from "@/features/agents/useAgents";
import { useCreateAgent, useUpdateAgent, useDeleteAgent } from "./useAdminAgents";

export function AdminAgents() {
  const { data: agents, isLoading } = useAgents();
  const createAgent = useCreateAgent();

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [key, setKey] = useState("");
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [systemPrompt, setSystemPrompt] = useState("");

  function handleCreate(e: FormEvent) {
    e.preventDefault();
    if (!key.trim() || !name.trim() || !systemPrompt.trim()) return;
    createAgent.mutate(
      { key: key.trim(), name: name.trim(), description: description.trim(), systemPrompt: systemPrompt.trim() },
      {
        onSuccess: () => {
          setKey("");
          setName("");
          setDescription("");
          setSystemPrompt("");
          setShowCreateForm(false);
        },
      }
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">AI Assistants & Personas</h2>
          <p className="mt-1 text-sm text-slate-500">
            Configure specialized assistants with tailored domain instructions (HR, IT, Finance, Engineering).
          </p>
        </div>
        <button
          onClick={() => setShowCreateForm((v) => !v)}
          className="flex items-center gap-2 rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-bold text-white hover:bg-slate-700 shadow-sm transition-colors"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 4v16m8-8H4" />
          </svg>
          {showCreateForm ? "Cancel" : "New Assistant"}
        </button>
      </div>

      {showCreateForm && (
        <form onSubmit={handleCreate} className="space-y-4 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-base font-bold text-slate-900">Create New AI Assistant</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">Key Identifier</label>
              <input
                value={key}
                onChange={(e) => setKey(e.target.value)}
                placeholder="e.g. legal"
                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:border-blue-500 focus:bg-white focus:outline-none transition-all"
              />
              <p className="mt-1 text-xs text-slate-400">Lowercase, unique identifier.</p>
            </div>
            <div>
              <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">Display Name</label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Legal & Compliance Advisor"
                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:border-blue-500 focus:bg-white focus:outline-none transition-all"
              />
            </div>
          </div>
          <div>
            <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">Description</label>
            <input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Short description shown to users when selecting an assistant"
              className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:border-blue-500 focus:bg-white focus:outline-none transition-all"
            />
          </div>
          <div>
            <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">System Prompt</label>
            <textarea
              value={systemPrompt}
              onChange={(e) => setSystemPrompt(e.target.value)}
              rows={4}
              placeholder="Detailed instructions, scope limitations, and personality for this assistant..."
              className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:border-blue-500 focus:bg-white focus:outline-none transition-all"
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setShowCreateForm(false)}
              className="rounded-xl border border-slate-200 px-5 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={createAgent.isPending || !key.trim() || !name.trim() || !systemPrompt.trim()}
              className="rounded-xl bg-slate-900 px-6 py-2.5 text-sm font-bold text-white hover:bg-slate-700 disabled:opacity-40 transition-colors"
            >
              {createAgent.isPending ? "Creating…" : "Save Assistant"}
            </button>
          </div>
          {createAgent.isError && (
            <p className="text-sm text-red-600 font-medium">Couldn't create assistant. The key may already be taken.</p>
          )}
        </form>
      )}

      {isLoading && (
        <div className="flex items-center justify-center py-16 text-slate-400 gap-3">
          <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
          Loading assistants…
        </div>
      )}

      <div className="grid grid-cols-1 gap-4">
        {agents?.map((agent) => (
          <AgentCard key={agent.id} agent={agent} />
        ))}
      </div>
    </div>
  );
}

function AgentCard({ agent }: { agent: Agent }) {
  const [editing, setEditing] = useState(false);
  const [name, setName] = useState(agent.name);
  const [description, setDescription] = useState(agent.description || "");
  const [systemPrompt, setSystemPrompt] = useState(agent.systemPrompt);

  const updateAgent = useUpdateAgent();
  const deleteAgent = useDeleteAgent();

  function handleSave() {
    updateAgent.mutate(
      { id: agent.id, name: name.trim(), description: description.trim(), systemPrompt: systemPrompt.trim() },
      { onSuccess: () => setEditing(false) }
    );
  }

  function handleDelete() {
    if (confirm(`Delete "${agent.name}"? Existing chats will fall back to the general assistant.`)) {
      deleteAgent.mutate(agent.id);
    }
  }

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-start gap-3 flex-1 min-w-0">
          <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center shrink-0 mt-0.5">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2.5 flex-wrap">
              {editing ? (
                <input
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="rounded-lg border border-slate-300 px-3 py-1 text-base font-bold text-slate-900"
                />
              ) : (
                <h3 className="text-base font-bold text-slate-900">{agent.name}</h3>
              )}
              <span className="rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-mono text-slate-500 font-semibold">
                key: {agent.key}
              </span>
            </div>
            {!editing && agent.description && (
              <p className="mt-1 text-sm text-slate-500 leading-relaxed">{agent.description}</p>
            )}
          </div>
        </div>
        <div className="flex shrink-0 gap-2">
          {!editing ? (
            <>
              <button
                onClick={() => setEditing(true)}
                className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
              >
                Edit
              </button>
              {agent.key !== "general" && (
                <button
                  onClick={handleDelete}
                  className="rounded-lg border border-red-200 px-3 py-1.5 text-xs font-semibold text-red-600 hover:bg-red-50 transition-colors"
                >
                  Delete
                </button>
              )}
            </>
          ) : (
            <div className="flex gap-2">
              <button
                onClick={() => {
                  setEditing(false);
                  setName(agent.name);
                  setDescription(agent.description || "");
                  setSystemPrompt(agent.systemPrompt);
                }}
                className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleSave}
                disabled={updateAgent.isPending}
                className="rounded-lg bg-slate-900 px-4 py-1.5 text-xs font-bold text-white hover:bg-slate-700 disabled:opacity-40 transition-colors"
              >
                {updateAgent.isPending ? "Saving…" : "Save"}
              </button>
            </div>
          )}
        </div>
      </div>

      {editing && (
        <div className="mt-5 space-y-4 border-t border-slate-100 pt-5">
          <div>
            <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">Description</label>
            <input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:bg-white focus:outline-none transition-all"
            />
          </div>
          <div>
            <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">System Prompt</label>
            <textarea
              value={systemPrompt}
              onChange={(e) => setSystemPrompt(e.target.value)}
              rows={4}
              className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:bg-white focus:outline-none transition-all"
            />
          </div>
        </div>
      )}
    </div>
  );
}
