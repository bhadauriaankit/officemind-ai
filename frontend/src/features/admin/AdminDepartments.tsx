import { FormEvent, useState } from "react";
import { useAdminDepartments, useCreateDepartment, useDeleteDepartment } from "./useAdminDepartments";

export function AdminDepartments() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useAdminDepartments(page, 20);
  const createDept = useCreateDepartment();
  const deleteDept = useDeleteDepartment();

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [showAdd, setShowAdd] = useState(false);

  function handleCreate(e: FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    createDept.mutate(
      { name: name.trim(), description: description.trim() },
      {
        onSuccess: () => {
          setName("");
          setDescription("");
          setShowAdd(false);
        },
      }
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Departments</h2>
          <p className="mt-1 text-sm text-slate-500">
            Define organizational boundaries for document access and team partitioning.
          </p>
        </div>
        <button
          onClick={() => setShowAdd((s) => !s)}
          className="flex items-center gap-2 rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-bold text-white hover:bg-slate-700 shadow-sm transition-colors"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 4v16m8-8H4" />
          </svg>
          {showAdd ? "Cancel" : "Add Department"}
        </button>
      </div>

      {showAdd && (
        <form onSubmit={handleCreate} className="space-y-4 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <h3 className="text-base font-bold text-slate-900">New Department</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">Department Name</label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Legal & Compliance"
                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:border-blue-500 focus:bg-white focus:outline-none transition-all"
              />
            </div>
            <div>
              <label className="mb-1 block text-xs font-bold uppercase tracking-wider text-slate-600">Description</label>
              <input
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Responsibilities, scope, or mandate"
                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm text-slate-800 focus:border-blue-500 focus:bg-white focus:outline-none transition-all"
              />
            </div>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setShowAdd(false)}
              className="rounded-xl border border-slate-200 px-5 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={createDept.isPending || !name.trim()}
              className="rounded-xl bg-slate-900 px-6 py-2.5 text-sm font-bold text-white hover:bg-slate-700 disabled:opacity-40 transition-colors"
            >
              {createDept.isPending ? "Creating…" : "Save Department"}
            </button>
          </div>
          {createDept.isError && (
            <p className="text-sm text-red-600 font-medium">Couldn't create department. Name may already exist.</p>
          )}
        </form>
      )}

      {isLoading && (
        <div className="flex items-center justify-center py-16 text-slate-400 gap-3">
          <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
          Loading departments…
        </div>
      )}

      {isError && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700 font-medium">
          Failed to load departments. Check your connection or permissions.
        </div>
      )}

      {data && (
        <>
          <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-100 bg-slate-50 text-xs font-bold uppercase tracking-wider text-slate-500">
                <tr>
                  <th className="px-5 py-3.5">Department</th>
                  <th className="px-5 py-3.5">Description</th>
                  <th className="px-5 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.items.map((dept) => (
                  <tr key={dept.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-5 py-4 font-bold text-slate-900">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center shrink-0">
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                          </svg>
                        </div>
                        {dept.name}
                      </div>
                    </td>
                    <td className="px-5 py-4 text-slate-500 leading-relaxed max-w-md">{dept.description || "—"}</td>
                    <td className="px-5 py-4 text-right">
                      <button
                        disabled={deleteDept.isPending}
                        onClick={() => {
                          if (confirm(`Delete department "${dept.name}"?`)) {
                            deleteDept.mutate(dept.id);
                          }
                        }}
                        className="rounded-lg border border-red-200 px-3 py-1.5 text-xs font-semibold text-red-600 hover:bg-red-50 transition-colors disabled:opacity-40"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
                {data.items.length === 0 && (
                  <tr>
                    <td colSpan={3} className="px-5 py-12 text-center text-sm text-slate-400">
                      No departments configured yet. Click "Add Department" to create one.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {data.totalPages > 1 && (
            <div className="flex items-center justify-between text-sm text-slate-500">
              <span>Page {data.page + 1} of {data.totalPages} — {data.totalElements} departments total</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(p - 1, 0))}
                  disabled={data.page === 0}
                  className="rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold disabled:opacity-40 hover:bg-slate-50 transition-colors"
                >
                  ← Previous
                </button>
                <button
                  onClick={() => setPage((p) => p + 1)}
                  disabled={data.page + 1 >= data.totalPages}
                  className="rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold disabled:opacity-40 hover:bg-slate-50 transition-colors"
                >
                  Next →
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
