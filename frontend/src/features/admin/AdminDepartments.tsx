import { FormEvent, useState } from "react";
import { useAdminDepartments, useCreateDepartment, useDeleteDepartment } from "./useAdminDepartments";

export function AdminDepartments() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useAdminDepartments(page, 20);
  const createDept = useCreateDepartment();
  const deleteDept = useDeleteDepartment();

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  function handleCreate(e: FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    createDept.mutate(
      { name: name.trim(), description: description.trim() },
      { onSuccess: () => { setName(""); setDescription(""); } }
    );
  }

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-slate-900">Departments</h2>

      <form onSubmit={handleCreate} className="flex gap-2 rounded-lg border border-slate-200 bg-white p-4">
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Department name"
          className="flex-1 rounded-md border border-slate-300 px-3 py-2 text-sm"
        />
        <input
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Description (optional)"
          className="flex-1 rounded-md border border-slate-300 px-3 py-2 text-sm"
        />
        <button
          type="submit"
          disabled={createDept.isPending || !name.trim()}
          className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
        >
          Add
        </button>
      </form>
      {createDept.isError && (
        <p className="text-sm text-red-600">Couldn't create department (name may already exist).</p>
      )}

      {isLoading && <p className="text-sm text-slate-500">Loading departments…</p>}
      {isError && <p className="text-sm text-red-600">Couldn't load departments.</p>}

      {data && (
        <>
          <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-200 bg-slate-50 text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-4 py-3">Name</th>
                  <th className="px-4 py-3">Description</th>
                  <th className="px-4 py-3">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.items.map((dept) => (
                  <tr key={dept.id}>
                    <td className="px-4 py-3 font-medium text-slate-800">{dept.name}</td>
                    <td className="px-4 py-3 text-slate-500">{dept.description || "—"}</td>
                    <td className="px-4 py-3">
                      <button
                        disabled={deleteDept.isPending}
                        onClick={() => deleteDept.mutate(dept.id)}
                        className="text-xs font-medium text-red-600 hover:text-red-800 disabled:opacity-40"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
                {data.items.length === 0 && (
                  <tr>
                    <td colSpan={3} className="px-4 py-6 text-center text-slate-400">
                      No departments yet.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="flex items-center justify-between text-sm text-slate-500">
            <span>
              Page {data.page + 1} of {Math.max(data.totalPages, 1)} — {data.totalElements} total
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => setPage((p) => Math.max(p - 1, 0))}
                disabled={data.page === 0}
                className="rounded-md border border-slate-300 px-3 py-1 disabled:opacity-40"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={data.page + 1 >= data.totalPages}
                className="rounded-md border border-slate-300 px-3 py-1 disabled:opacity-40"
              >
                Next
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
