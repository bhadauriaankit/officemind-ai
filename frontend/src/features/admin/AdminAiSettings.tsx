import { FormEvent, useEffect, useState } from "react";
import { useAdminAiSettings, useUpdateAiSettings } from "./useAdminAiSettings";

export function AdminAiSettings() {
  const { data, isLoading, isError } = useAdminAiSettings();
  const updateSettings = useUpdateAiSettings();

  const [modelName, setModelName] = useState("");
  const [temperature, setTemperature] = useState(0.7);
  const [systemPrompt, setSystemPrompt] = useState("");

  // Seed the form once the current settings load, so edits aren't
  // clobbered on every background refetch.
  useEffect(() => {
    if (data) {
      setModelName(data.modelName);
      setTemperature(data.temperature);
      setSystemPrompt(data.systemPrompt || "");
    }
  }, [data]);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!modelName.trim()) return;
    updateSettings.mutate({
      modelName: modelName.trim(),
      temperature,
      systemPrompt: systemPrompt.trim(),
    });
  }

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h2 className="text-xl font-bold text-slate-900">AI Configuration</h2>
        <p className="mt-1 text-sm text-slate-500">
          Controls the model, temperature, and system prompt used for every new
          message sent through the chat assistant.
        </p>
      </div>

      {isLoading && <p className="text-sm text-slate-500">Loading settings…</p>}
      {isError && <p className="text-sm text-red-600">Couldn't load AI settings.</p>}

      {data && (
        <form
          onSubmit={handleSubmit}
          className="space-y-5 rounded-lg border border-slate-200 bg-white p-6"
        >
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Model name
            </label>
            <input
              value={modelName}
              onChange={(e) => setModelName(e.target.value)}
              placeholder="e.g. llama3.2:1b"
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
            <p className="mt-1 text-xs text-slate-400">
              Must match a model already pulled into Ollama (run{" "}
              <code className="rounded bg-slate-100 px-1">docker exec officemind-ollama ollama list</code>{" "}
              to check). Pulling a new model isn't done from this screen.
            </p>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Temperature: {temperature.toFixed(2)}
            </label>
            <input
              type="range"
              min="0"
              max="2"
              step="0.05"
              value={temperature}
              onChange={(e) => setTemperature(parseFloat(e.target.value))}
              className="w-full"
            />
            <div className="mt-1 flex justify-between text-xs text-slate-400">
              <span>0.0 — focused &amp; deterministic</span>
              <span>2.0 — creative &amp; unpredictable</span>
            </div>
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              System prompt
            </label>
            <textarea
              value={systemPrompt}
              onChange={(e) => setSystemPrompt(e.target.value)}
              rows={5}
              placeholder="Instructions the assistant follows in every conversation…"
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
            />
          </div>

          <div className="flex items-center justify-between border-t border-slate-100 pt-4">
            <span className="text-xs text-slate-400">
              Last updated {new Date(data.updatedAt).toLocaleString()}
            </span>
            <button
              type="submit"
              disabled={updateSettings.isPending || !modelName.trim()}
              className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
            >
              {updateSettings.isPending ? "Saving…" : "Save changes"}
            </button>
          </div>

          {updateSettings.isSuccess && (
            <p className="text-sm text-green-600">Settings saved.</p>
          )}
          {updateSettings.isError && (
            <p className="text-sm text-red-600">Couldn't save settings. Please try again.</p>
          )}
        </form>
      )}
    </div>
  );
}
