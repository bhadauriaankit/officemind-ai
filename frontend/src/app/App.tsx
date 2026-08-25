import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import { PlatformHealthPanel } from "@/features/system/PlatformHealthPanel";
import { AuthPanel } from "@/features/system/AuthPanel";
import { AdminLayout } from "@/features/admin/AdminLayout";
import { AdminDashboard } from "@/features/admin/AdminDashboard";
import { AdminUsers } from "@/features/admin/AdminUsers";
import { AdminDepartments } from "@/features/admin/AdminDepartments";
import { RequireAdmin } from "@/shared/auth/RequireAdmin";

function Home() {
  return (
    <div className="min-h-screen bg-slate-50 p-8">
      <header className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">OfficeMind AI</h1>
        <p className="text-sm text-slate-500">Private enterprise AI assistant</p>
      </header>
      <main className="max-w-md space-y-6">
        <AuthPanel />
        <PlatformHealthPanel />
        <Link to="/admin" className="block text-sm font-medium text-slate-600 hover:text-slate-900">
          Go to Admin Portal →
        </Link>
      </main>
    </div>
  );
}

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route
          path="/admin"
          element={
            <RequireAdmin>
              <AdminLayout />
            </RequireAdmin>
          }
        >
          <Route index element={<AdminDashboard />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="departments" element={<AdminDepartments />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
