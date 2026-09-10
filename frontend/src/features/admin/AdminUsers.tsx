import { useState } from "react";
import { AdminUser, useAdminUsers, useSetUserStatus, useUpdateUserRoles } from "./useAdminUsers";

const ALL_ROLES = ["ADMIN", "HR", "FINANCE", "IT", "DEVELOPER", "EMPLOYEE"] as const;

const ROLE_COLORS: Record<string, string> = {
  ADMIN:    "bg-purple-100 text-purple-700",
  HR:       "bg-pink-100 text-pink-700",
  FINANCE:  "bg-amber-100 text-amber-700",
  IT:       "bg-blue-100 text-blue-700",
  DEVELOPER:"bg-indigo-100 text-indigo-700",
  EMPLOYEE: "bg-slate-100 text-slate-600",
};

// ── Create User Modal ────────────────────────────────────────────────────────
// Note: OfficeMind manages users through Keycloak. New users are provisioned
// automatically on first login. Admins can invite users by sending them the
// Keycloak registration link, or by creating them directly in Keycloak Admin.
function CreateUserModal({ onClose }: { onClose: () => void }) {
  const keycloakAdminUrl = "http://localhost:8081/admin/master/console/#/officemind/users/add-user";

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="w-full max-w-lg rounded-2xl bg-white shadow-2xl border border-slate-200">
        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <h2 className="text-lg font-bold text-slate-900">Add New User</h2>
          <button onClick={onClose} className="rounded-lg p-1.5 text-slate-400 hover:bg-slate-100 hover:text-slate-700 transition-colors">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <div className="p-6 space-y-5">
          <div className="rounded-xl bg-blue-50 border border-blue-200 p-4">
            <div className="flex gap-3">
              <svg className="w-5 h-5 text-blue-600 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              <div>
                <p className="text-sm font-semibold text-blue-800">How users are created</p>
                <p className="mt-1 text-sm text-blue-700">
                  OfficeMind uses Keycloak for identity management. Users are auto-provisioned on first login. To create a user manually, use the Keycloak Admin Console.
                </p>
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <p className="text-sm font-semibold text-slate-700">Option 1 — Share login link</p>
            <p className="text-sm text-slate-500">Send employees the app URL. They register through Keycloak and are automatically added to OfficeMind on first sign-in.</p>

            <p className="text-sm font-semibold text-slate-700 pt-2">Option 2 — Create directly in Keycloak</p>
            <p className="text-sm text-slate-500">Open the Keycloak Admin Console to create users, set temporary passwords, and assign realm roles.</p>

            <a
              href={keycloakAdminUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center justify-center gap-2 w-full rounded-xl bg-slate-900 px-4 py-3 text-sm font-bold text-white hover:bg-slate-700 transition-colors mt-2"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" /></svg>
              Open Keycloak Admin Console
            </a>
          </div>
        </div>
        <div className="flex justify-end border-t border-slate-100 px-6 py-4">
          <button onClick={onClose} className="rounded-xl border border-slate-200 px-5 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors">
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Role Edit Modal ──────────────────────────────────────────────────────────
function EditRolesModal({
  user, onClose, onSave, busy,
}: {
  user: AdminUser;
  onClose: () => void;
  onSave: (roles: string[]) => void;
  busy: boolean;
}) {
  const [selected, setSelected] = useState<string[]>(user.roles);

  const toggle = (role: string) => {
    setSelected((prev) =>
      prev.includes(role) ? prev.filter((r) => r !== role) : [...prev, role]
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="w-full max-w-sm rounded-2xl bg-white shadow-2xl border border-slate-200">
        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <div>
            <h2 className="text-base font-bold text-slate-900">Edit Roles</h2>
            <p className="text-xs text-slate-500 mt-0.5">{user.displayName} · {user.email}</p>
          </div>
          <button onClick={onClose} className="rounded-lg p-1.5 text-slate-400 hover:bg-slate-100 transition-colors">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <div className="p-6 space-y-2">
          {ALL_ROLES.map((role) => (
            <label key={role} className="flex items-center gap-3 rounded-xl px-4 py-3 hover:bg-slate-50 cursor-pointer transition-colors border border-transparent hover:border-slate-200">
              <input
                type="checkbox"
                checked={selected.includes(role)}
                onChange={() => toggle(role)}
                className="h-4 w-4 rounded border-slate-300 text-slate-900 accent-slate-900"
              />
              <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${ROLE_COLORS[role]}`}>{role}</span>
            </label>
          ))}
        </div>
        <div className="flex items-center justify-between border-t border-slate-100 px-6 py-4 gap-3">
          <button onClick={onClose} className="flex-1 rounded-xl border border-slate-200 px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors">
            Cancel
          </button>
          <button
            onClick={() => { if (selected.length > 0) { onSave(selected); onClose(); } }}
            disabled={busy || selected.length === 0}
            className="flex-1 rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-bold text-white hover:bg-slate-700 disabled:opacity-40 transition-colors"
          >
            Save Roles
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Main Component ───────────────────────────────────────────────────────────
export function AdminUsers() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingUser, setEditingUser] = useState<AdminUser | null>(null);

  const { data, isLoading, isError } = useAdminUsers(page, 20);
  const setStatus = useSetUserStatus();
  const updateRoles = useUpdateUserRoles();
  const busy = setStatus.isPending || updateRoles.isPending;

  const filtered = data?.items.filter((u) => {
    const q = search.toLowerCase();
    return (
      !q ||
      u.displayName.toLowerCase().includes(q) ||
      u.email.toLowerCase().includes(q) ||
      u.roles.some((r) => r.toLowerCase().includes(q))
    );
  }) ?? [];

  return (
    <div className="space-y-6">
      {showCreateModal && <CreateUserModal onClose={() => setShowCreateModal(false)} />}
      {editingUser && (
        <EditRolesModal
          user={editingUser}
          onClose={() => setEditingUser(null)}
          onSave={(roles) => updateRoles.mutate({ userId: editingUser.id, roles })}
          busy={busy}
        />
      )}

      {/* Header */}
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Users & Roles</h2>
          <p className="text-sm text-slate-500 mt-1">Manage employee access and permissions.</p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2 rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-bold text-white hover:bg-slate-700 shadow-sm transition-colors"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 4v16m8-8H4" /></svg>
          Add User
        </button>
      </div>

      {/* Search */}
      <div className="relative">
        <svg className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by name, email or role…"
          className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2.5 pl-10 pr-4 text-sm text-slate-800 focus:border-slate-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all"
        />
      </div>

      {/* Table */}
      {isLoading && (
        <div className="flex items-center justify-center py-16 text-slate-400 gap-3">
          <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
          Loading users…
        </div>
      )}
      {isError && <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700 font-medium">Failed to load users. Check your connection or permissions.</div>}

      {!isLoading && !isError && (
        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-slate-100 bg-slate-50 text-xs font-bold uppercase tracking-wider text-slate-500">
              <tr>
                <th className="px-5 py-3.5">User</th>
                <th className="px-5 py-3.5">Roles</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-5 py-12 text-center text-sm text-slate-400">
                    {search ? `No users matching "${search}"` : "No users found."}
                  </td>
                </tr>
              )}
              {filtered.map((user) => {
                const initials = user.displayName.split(" ").map((w) => w[0]).join("").slice(0, 2).toUpperCase();
                return (
                  <tr key={user.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-5 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-full bg-gradient-to-br from-slate-700 to-slate-900 flex items-center justify-center text-white text-xs font-bold shrink-0 shadow-sm">
                          {initials}
                        </div>
                        <div>
                          <p className="font-semibold text-slate-800 text-sm">{user.displayName}</p>
                          <p className="text-xs text-slate-400">{user.email}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-5 py-4">
                      <div className="flex flex-wrap gap-1.5">
                        {user.roles.map((r) => (
                          <span key={r} className={`rounded-full px-2.5 py-0.5 text-xs font-bold ${ROLE_COLORS[r] ?? "bg-slate-100 text-slate-600"}`}>{r}</span>
                        ))}
                      </div>
                    </td>
                    <td className="px-5 py-4">
                      <span className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-bold ${user.status === "ACTIVE" ? "bg-emerald-100 text-emerald-700" : "bg-red-100 text-red-700"}`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${user.status === "ACTIVE" ? "bg-emerald-500" : "bg-red-500"}`}></span>
                        {user.status}
                      </span>
                    </td>
                    <td className="px-5 py-4">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => setEditingUser(user)}
                          className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 hover:border-slate-300 transition-colors"
                        >
                          Edit Roles
                        </button>
                        <button
                          disabled={busy}
                          onClick={() => setStatus.mutate({ userId: user.id, action: user.status === "ACTIVE" ? "disable" : "reactivate" })}
                          className={`rounded-lg border px-3 py-1.5 text-xs font-semibold transition-colors disabled:opacity-40 ${user.status === "ACTIVE" ? "border-red-200 text-red-600 hover:bg-red-50" : "border-emerald-200 text-emerald-600 hover:bg-emerald-50"}`}
                        >
                          {user.status === "ACTIVE" ? "Disable" : "Activate"}
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-between text-sm text-slate-500">
          <span>Page {data.page + 1} of {data.totalPages} — {data.totalElements} users total</span>
          <div className="flex gap-2">
            <button onClick={() => setPage((p) => Math.max(p - 1, 0))} disabled={data.page === 0} className="rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold disabled:opacity-40 hover:bg-slate-50 transition-colors">← Previous</button>
            <button onClick={() => setPage((p) => p + 1)} disabled={data.page + 1 >= data.totalPages} className="rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold disabled:opacity-40 hover:bg-slate-50 transition-colors">Next →</button>
          </div>
        </div>
      )}
    </div>
  );
}
