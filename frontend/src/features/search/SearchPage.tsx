import { FormEvent } from "react";
import { useSearch } from "./useSearch";
import { Link } from "react-router-dom";

export function SearchPage() {
  const { query, setQuery, limit, setLimit, results, isSearching, isError, search, reset } =
    useSearch();

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    reset();
    search();
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white px-8 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-lg font-bold text-slate-900">OfficeMind AI — Enterprise Search</h1>
            <p className="text-xs text-slate-500">Semantic search across all indexed company documents</p>
          </div>
          <Link to="/chat" className="text-sm font-medium text-slate-500 hover:text-slate-800">
            Go to Chat →
          </Link>
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-6 py-8">
        <form onSubmit={handleSubmit} className="mb-8 flex gap-3">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search company documents..."
            className="flex-1 rounded-md border border-slate-300 px-4 py-2 text-sm focus:border-slate-500 focus:outline-none"
          />
          <select
            value={limit}
            onChange={(e) => setLimit(Number(e.target.value))}
            className="rounded-md border border-slate-300 bg-white px-3 py-2 text-sm"
          >
            <option value={5}>5 results</option>
            <option value={10}>10 results</option>
            <option value={20}>20 results</option>
          </select>
          <button
            type="submit"
            disabled={isSearching || !query.trim()}
            className="rounded-md bg-slate-900 px-5 py-2 text-sm font-medium text-white disabled:opacity-40 hover:bg-slate-700"
          >
            {isSearching ? "Searching..." : "Search"}
          </button>
        </form>

        {isError && (
          <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            Search failed. Make sure at least one document has been indexed.
          </div>
        )}

        {results.length === 0 && !isSearching && query && (
          <p className="text-center text-sm text-slate-400">No results found. Try a different query.</p>
        )}

        <div className="space-y-4">
          {results.map((r, i) => (
            <div
              key={r.chunkId}
              className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm"
            >
              <div className="mb-2 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="rounded bg-slate-100 px-2 py-0.5 text-xs font-mono font-medium text-slate-600">
                    #{i + 1}
                  </span>
                  <span className="text-sm font-semibold text-slate-800">{r.documentFileName}</span>
                  <span className="text-xs text-slate-400">chunk {r.chunkIndex + 1}</span>
                </div>
                <span
                  className={`rounded px-2 py-0.5 text-xs font-medium ${
                    r.score >= 0.6
                      ? "bg-green-100 text-green-700"
                      : r.score >= 0.4
                      ? "bg-amber-100 text-amber-700"
                      : "bg-slate-100 text-slate-500"
                  }`}
                >
                  score {r.score.toFixed(3)}
                </span>
              </div>
              <p className="text-sm leading-relaxed text-slate-600">{r.content}</p>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
