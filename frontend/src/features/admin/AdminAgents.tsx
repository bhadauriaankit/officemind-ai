
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
    <div className="max-w-3xl space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Agents</h2>
          <p className="mt-1 text-sm text-slate-500">
            Named personas with their own system prompt, selectable when starting a chat.
            The agent's prompt takes priority over the global AI Configuration prompt.
          </p>
        </div>
        <button
          onClick={() => setShowCreateForm((v) => !v)}
          className="whitespace-nowrap rounded-md bg-slate-900 px-3 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          {showCreateForm ? "Cancel" : "+ New agent"}
        </button>
      </div>

      {showCreateForm && (
        <form onSubmit={handleCreate} className="space-y-4 rounded-lg border border-slate-200 bg-white p-6">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Key</label>
            <input
              value={key}
              onChange={(e) => setKey(e.target.value)}
              placeholder="e.g. legal"
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
            <p className="mt-1 text-xs text-slate-400">
              Short, stable identifier. Lowercase, no spaces. Can't be changed after creation.
            </p>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Name</label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. Legal Assistant"
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Description</label>
            <input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Shown to users picking an agent"
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">System prompt</label>
            <textarea
              value={systemPrompt}
              onChange={(e) => setSystemPrompt(e.target.value)}
              rows={4}
              placeholder="Instructions this agent follows..."
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
          </div>
          <div className="flex justify-end">
            <button
              type="submit"
              disabled={createAgent.isPending}
              className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
            >
              {createAgent.isPending ? "Creating…" : "Create agent"}
            </button>
          </div>
          {createAgent.isError && (
            <p className="text-sm text-red-600">Couldn't create agent. Check the key isn't already taken.</p>
          )}
        </form>
      )}

      {isLoading && <p className="text-sm text-slate-500">Loading agents…</p>}

      <div className="space-y-3">
        {agents?.map((agent) => (
          <AgentRow key={agent.id} agent={agent} />
        ))}
      </div>
    </div>
  );
}

function AgentRow({ agent }: { agent: Agent }) {
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
    if (confirm(`Delete "${agent.name}"? Conversations that used it will fall back to the default prompt.`)) {
      deleteAgent.mutate(agent.id);
    }
  }

  return (
    <div className="rounded-lg border border-slate-200 bg-white p-4">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2">
            <span className="rounded bg-slate-100 px-2 py-0.5 text-xs font-mono text-slate-500">{agent.key}</span>
            {editing ? (
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="rounded-md border border-slate-300 px-2 py-1 text-sm font-semibold"
              />
            ) : (
              <span className="font-semibold text-slate-900">{agent.name}</span>
            )}
          </div>
          {!editing && agent.description && (
            <p className="mt-1 text-sm text-slate-500">{agent.description}</p>
          )}
        </div>
        <div className="flex shrink-0 gap-2">
          {!editing && (
            <>
              <button onClick={() => setEditing(true)} className="text-sm font-medium text-slate-600 hover:text-slate-900">
                Edit
              </button>
              <button onClick={handleDelete} className="text-sm font-medium text-red-500 hover:text-red-700">
                Delete
              </button>
            </>
          )}
        </div>
      </div>

      {editing && (
        <div className="mt-4 space-y-3 border-t border-slate-100 pt-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Description</label>
            <input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">System prompt</label>
            <textarea
              value={systemPrompt}
              onChange={(e) => setSystemPrompt(e.target.value)}
              rows={4}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
          </div>
          <div className="flex justify-end gap-2">
            <button
              onClick={() => {
                setEditing(false);
                setName(agent.name);
                setDescription(agent.description || "");
                setSystemPrompt(agent.systemPrompt);
              }}
              className="rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100"
            >
              Cancel
            </button>
            <button
              onClick={handleSave}
              disabled={updateAgent.isPending}
              className="rounded-md bg-slate-900 px-3 py-2 text-sm font-medium text-white disabled:opacity-40"
            >
              {updateAgent.isPending ? "Saving…" : "Save"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
