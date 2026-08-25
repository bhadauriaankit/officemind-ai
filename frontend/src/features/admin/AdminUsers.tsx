import { useState } from "react";
import { AdminUser, useAdminUsers, useSetUserStatus, useUpdateUserRoles } from "./useAdminUsers";

const ALL_ROLES = ["ADMIN", "HR", "FINANCE", "IT", "DEVELOPER", "EMPLOYEE"] as const;

export function AdminUsers() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useAdminUsers(page, 20);
  const setStatus = useSetUserStatus();
  const updateRoles = useUpdateUserRoles();

  if (isLoading) return <p className="text-sm text-slate-500">Loading users…</p>;
  if (isError) return <p className="text-sm text-red-600">Couldn't load users.</p>;
  if (!data) return null;

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold text-slate-900">Users</h2>

      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-slate-200 bg-slate-50 text-xs uppercase text-slate-500">
            <tr>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Roles</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.items.map((user) => (
              <UserRow
                key={user.id}
                user={user}
                onToggleStatus={() =>
                  setStatus.mutate({
                    userId: user.id,
                    action: user.status === "ACTIVE" ? "disable" : "reactivate",
                  })
                }
                onToggleRole={(role) => {
                  const has = user.roles.includes(role);
                  const nextRoles = has
                    ? user.roles.filter((r) => r !== role)
                    : [...user.roles, role];
                  if (nextRoles.length === 0) return; // must keep at least one role
                  updateRoles.mutate({ userId: user.id, roles: nextRoles });
                }}
                busy={setStatus.isPending || updateRoles.isPending}
              />
            ))}
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
    </div>
  );
}

function UserRow({
  user,
  onToggleStatus,
  onToggleRole,
  busy,
}: {
  user: AdminUser;
  onToggleStatus: () => void;
  onToggleRole: (role: string) => void;
  busy: boolean;
}) {
  return (
    <tr>
      <td className="px-4 py-3 font-medium text-slate-800">{user.displayName}</td>
      <td className="px-4 py-3 text-slate-500">{user.email}</td>
      <td className="px-4 py-3">
        <div className="flex flex-wrap gap-1">
          {ALL_ROLES.map((role) => {
            const active = user.roles.includes(role);
            return (
              <button
                key={role}
                disabled={busy}
                onClick={() => onToggleRole(role)}
                className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                  active
                    ? "bg-slate-900 text-white"
                    : "bg-slate-100 text-slate-400 hover:bg-slate-200"
                }`}
              >
                {role}
              </button>
            );
          })}
        </div>
      </td>
      <td className="px-4 py-3">
        <span
          className={`rounded-full px-2 py-0.5 text-xs font-medium ${
            user.status === "ACTIVE"
              ? "bg-emerald-100 text-emerald-700"
              : "bg-red-100 text-red-700"
          }`}
        >
          {user.status}
        </span>
      </td>
      <td className="px-4 py-3">
        <button
          disabled={busy}
          onClick={onToggleStatus}
          className="text-xs font-medium text-slate-600 hover:text-slate-900 disabled:opacity-40"
        >
          {user.status === "ACTIVE" ? "Disable" : "Reactivate"}
        </button>
      </td>
    </tr>
  );
}
