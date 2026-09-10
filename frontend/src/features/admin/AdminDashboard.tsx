import { Link } from "react-router-dom";
import { useAdminUsers } from "./useAdminUsers";
import { useAdminDocuments } from "./useAdminDocuments";
import { useAgents } from "@/features/agents/useAgents";
import { useAdminDepartments } from "./useAdminDepartments";
import { PlatformHealthPanel } from "@/features/system/PlatformHealthPanel";

export function AdminDashboard() {
  const { data: usersData } = useAdminUsers(0, 1);
  const { data: docsData } = useAdminDocuments(0, 1);
  const { data: agents } = useAgents();
  const { data: deptsData } = useAdminDepartments(0, 1);

  const stats = [
    {
      label: "Total Users",
      value: usersData?.totalElements ?? "—",
      sub: "Active in directory",
      icon: "M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z",
      color: "bg-blue-50 text-blue-600",
      to: "/admin/users",
    },
    {
      label: "Knowledge Documents",
      value: docsData?.totalElements ?? "—",
      sub: "Indexed in Qdrant",
      icon: "M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z",
      color: "bg-emerald-50 text-emerald-600",
      to: "/admin/documents",
    },
    {
      label: "AI Assistants",
      value: agents?.length ?? "—",
      sub: "Specialized personas",
      icon: "M14 10l-2 1m0 0l-2-1m2 1v2.5M20 7l-2 1m2-1l-2-1m2 1v2.5M14 4l-2-1-2 1M4 7l2-1M4 7l2 1M4 7v2.5M12 21l-2-1m2 1l2-1m-2 1v-2.5M6 18l-2-1v-2.5M18 18l2-1v-2.5",
      color: "bg-purple-50 text-purple-600",
      to: "/admin/agents",
    },
    {
      label: "Departments",
      value: deptsData?.totalElements ?? "—",
      sub: "Organizational units",
      icon: "M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4",
      color: "bg-amber-50 text-amber-600",
      to: "/admin/departments",
    },
  ];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">Admin Overview</h2>
        <p className="text-sm text-slate-500 mt-1">
          Monitor your private AI assistant deployment, knowledge base, and access controls.
        </p>
      </div>

      {/* Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat) => (
          <Link
            key={stat.label}
            to={stat.to}
            className="group rounded-2xl border border-slate-200 bg-white p-5 shadow-sm hover:shadow-md hover:border-blue-300 transition-all"
          >
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">{stat.label}</span>
              <div className={`w-8 h-8 rounded-xl flex items-center justify-center ${stat.color}`}>
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={stat.icon} />
                </svg>
              </div>
            </div>
            <p className="text-3xl font-extrabold text-slate-900 group-hover:text-blue-600 transition-colors">
              {stat.value}
            </p>
            <p className="text-xs font-medium text-slate-400 mt-1">{stat.sub}</p>
          </Link>
        ))}
      </div>

      {/* Main Grid: Health & Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Platform Health */}
        <div className="lg:col-span-1 rounded-2xl border border-slate-200 bg-slate-50/50 p-6 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold text-slate-900">Infrastructure Health</h3>
            <span className="flex items-center gap-1.5 text-xs font-bold text-emerald-700 bg-emerald-100 px-2.5 py-1 rounded-full">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
              Live
            </span>
          </div>
          <PlatformHealthPanel />
        </div>

        {/* Quick Management Shortcuts */}
        <div className="lg:col-span-2 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm space-y-4">
          <h3 className="text-base font-bold text-slate-900">Administrative Actions</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Link
              to="/admin/users"
              className="flex items-start gap-4 p-4 rounded-xl border border-slate-100 bg-slate-50 hover:bg-blue-50/50 hover:border-blue-200 transition-all group"
            >
              <div className="w-10 h-10 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center shrink-0">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-bold text-slate-800 group-hover:text-blue-600 transition-colors">Manage Users & Roles</p>
                <p className="text-xs text-slate-500 mt-1">Assign roles (HR, IT, Finance, Admin) or disable accounts.</p>
              </div>
            </Link>

            <Link
              to="/admin/documents"
              className="flex items-start gap-4 p-4 rounded-xl border border-slate-100 bg-slate-50 hover:bg-emerald-50/50 hover:border-emerald-200 transition-all group"
            >
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-bold text-slate-800 group-hover:text-emerald-600 transition-colors">Upload Documents</p>
                <p className="text-xs text-slate-500 mt-1">Index new company manuals, PDFs, and documentation.</p>
              </div>
            </Link>

            <Link
              to="/admin/agents"
              className="flex items-start gap-4 p-4 rounded-xl border border-slate-100 bg-slate-50 hover:bg-purple-50/50 hover:border-purple-200 transition-all group"
            >
              <div className="w-10 h-10 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center shrink-0">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-bold text-slate-800 group-hover:text-purple-600 transition-colors">Configure AI Personas</p>
                <p className="text-xs text-slate-500 mt-1">Tune specialized assistant system prompts and instructions.</p>
              </div>
            </Link>

            <Link
              to="/admin/ai-settings"
              className="flex items-start gap-4 p-4 rounded-xl border border-slate-100 bg-slate-50 hover:bg-amber-50/50 hover:border-amber-200 transition-all group"
            >
              <div className="w-10 h-10 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center shrink-0">
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
              </div>
              <div>
                <p className="text-sm font-bold text-slate-800 group-hover:text-amber-600 transition-colors">Global Model Settings</p>
                <p className="text-xs text-slate-500 mt-1">Adjust Ollama model name, temperature, and defaults.</p>
              </div>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
