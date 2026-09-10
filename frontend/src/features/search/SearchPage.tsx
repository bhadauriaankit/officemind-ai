import { FormEvent } from "react";
import { useSearch } from "./useSearch";
import { Link } from "react-router-dom";
import { NotificationBell } from "@/features/notifications/NotificationBell";

export function SearchPage() {
  const { query, setQuery, limit, setLimit, results, isSearching, isError, search, reset } =
    useSearch();

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    reset();
    search();
  }

  return (
    <div className="min-h-screen bg-slate-50 font-sans">
      <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/80 backdrop-blur-lg px-6 py-3 shadow-sm">
        <div className="mx-auto max-w-5xl flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-blue-600 text-white shadow-inner">
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <div>
              <h1 className="text-lg font-bold text-slate-900 leading-tight">OfficeMind Search</h1>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Link to="/" className="rounded-md px-3 py-1.5 text-sm font-semibold text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors">Home</Link>
            <Link to="/chat" className="rounded-md px-3 py-1.5 text-sm font-semibold text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors">Chat</Link>
            <div className="h-4 w-px bg-slate-300 mx-1"></div>
            <NotificationBell />
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-4xl px-6 py-12">
        <div className="text-center mb-10">
          <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight mb-3">Enterprise Knowledge Search</h2>
          <p className="text-slate-500 max-w-2xl mx-auto text-base">Instantly find answers across all indexed company documents, policies, and knowledge bases using semantic AI.</p>
        </div>

        <form onSubmit={handleSubmit} className="mb-10">
          <div className="flex flex-col sm:flex-row gap-3 bg-white p-2.5 rounded-2xl shadow-lg border border-slate-200 focus-within:ring-4 focus-within:ring-blue-500/20 focus-within:border-blue-500 transition-all">
            <div className="relative flex-1 flex items-center pl-3">
              <svg className="w-6 h-6 text-slate-400 absolute left-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Ask a question or search for topics..."
                className="w-full bg-transparent border-none px-12 py-3 text-slate-900 focus:outline-none focus:ring-0 placeholder-slate-400 text-lg font-medium"
              />
            </div>
            <div className="flex gap-2 w-full sm:w-auto">
              <select
                value={limit}
                onChange={(e) => setLimit(Number(e.target.value))}
                className="flex-1 sm:flex-none rounded-xl border-none bg-slate-50 px-5 py-3 text-sm font-semibold text-slate-700 focus:ring-0 hover:bg-slate-100 cursor-pointer outline-none"
              >
                <option value={5}>Top 5</option>
                <option value={10}>Top 10</option>
                <option value={20}>Top 20</option>
              </select>
              <button
                type="submit"
                disabled={isSearching || !query.trim()}
                className="flex-1 sm:flex-none rounded-xl bg-blue-600 px-8 py-3 text-sm font-bold text-white shadow-md disabled:opacity-50 hover:bg-blue-700 hover:shadow-lg transition-all"
              >
                {isSearching ? "Searching..." : "Search"}
              </button>
            </div>
          </div>
        </form>

        {isError && (
          <div className="mb-8 rounded-2xl border border-red-200 bg-red-50 px-6 py-5 text-sm text-red-700 flex items-start gap-4 shadow-sm">
            <svg className="w-6 h-6 text-red-500 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            <span className="font-medium">Search failed. Please ensure the backend is running and documents are indexed.</span>
          </div>
        )}

        {results.length === 0 && !isSearching && query && (
          <div className="text-center py-16 bg-white rounded-3xl border border-slate-200 border-dashed">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-slate-50 mb-4 shadow-inner">
              <svg className="w-8 h-8 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </div>
            <h3 className="text-xl font-bold text-slate-800">No results found</h3>
            <p className="mt-2 text-slate-500 max-w-md mx-auto">We couldn't find any documents matching your query. Try using different keywords or broader terms.</p>
          </div>
        )}

        <div className="space-y-6">
          {results.map((r, i) => (
            <div
              key={r.chunkId}
              className="group rounded-3xl border border-slate-200 bg-white p-6 md:p-8 shadow-sm hover:shadow-xl hover:-translate-y-1 hover:border-blue-300 transition-all duration-300"
            >
              <div className="mb-4 flex flex-col md:flex-row md:items-start justify-between gap-4">
                <div className="flex items-center gap-3 flex-wrap">
                  <span className="flex items-center justify-center w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 text-white text-xs font-bold shadow-sm">
                    {i + 1}
                  </span>
                  <span className="text-lg font-bold text-slate-900 group-hover:text-blue-600 transition-colors">{r.documentFileName}</span>
                  <span className="rounded-lg bg-slate-100 px-2.5 py-1 text-xs font-bold text-slate-500">CHUNK {r.chunkIndex + 1}</span>
                </div>
                <div className="flex items-center gap-2">
                  <span
                    className={`shrink-0 flex items-center gap-1.5 rounded-full px-3.5 py-1.5 text-xs font-bold shadow-sm border ${
                      r.score >= 0.6
                        ? "bg-emerald-50 text-emerald-700 border-emerald-200"
                        : r.score >= 0.4
                        ? "bg-amber-50 text-amber-700 border-amber-200"
                        : "bg-slate-50 text-slate-600 border-slate-200"
                    }`}
                  >
                    <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
                    Score: {(r.score * 100).toFixed(1)}%
                  </span>
                </div>
              </div>
              <div className="relative">
                <p className="text-base leading-relaxed text-slate-700 bg-slate-50 rounded-2xl p-5 border border-slate-100">{r.content}</p>
                <div className="absolute left-0 top-0 bottom-0 w-1 bg-blue-400 rounded-l-2xl opacity-0 group-hover:opacity-100 transition-opacity"></div>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
