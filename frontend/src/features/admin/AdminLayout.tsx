import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "react-oidc-context";

const navItems = [
  { to: "/admin", label: "Dashboard", end: true },
  { to: "/admin/users", label: "Users" },
  { to: "/admin/departments", label: "Departments" },
{ to: "/admin/documents", label: "Documents" },
];

export function AdminLayout() {
  const auth = useAuth();

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white px-8 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-lg font-bold text-slate-900">OfficeMind AI — Admin</h1>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-sm text-slate-500">
              {auth.user?.profile.name as string}
            </span>
            <button
              onClick={() => auth.removeUser()}
              className="text-sm font-medium text-slate-500 hover:text-slate-800"
            >
              Sign out
            </button>
          </div>
        </div>
      </header>

      <div className="flex">
        <nav className="w-48 border-r border-slate-200 bg-white p-4">
          <ul className="space-y-1">
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink
                  to={item.to}
                  end={item.end}
                  className={({ isActive }) =>
                    `block rounded-md px-3 py-2 text-sm font-medium ${
                      isActive
                        ? "bg-slate-900 text-white"
                        : "text-slate-600 hover:bg-slate-100"
                    }`
                  }
                >
                  {item.label}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        <main className="flex-1 p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
