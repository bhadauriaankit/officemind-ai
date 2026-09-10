import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import { PlatformHealthPanel } from "@/features/system/PlatformHealthPanel";
import { AuthPanel } from "@/features/system/AuthPanel";
import { AdminLayout } from "@/features/admin/AdminLayout";
import { AdminDashboard } from "@/features/admin/AdminDashboard";
import { AdminUsers } from "@/features/admin/AdminUsers";
import { AdminDepartments } from "@/features/admin/AdminDepartments";
import { AdminDocuments } from "@/features/admin/AdminDocuments";
import { AdminAiSettings } from "@/features/admin/AdminAiSettings";
import { AdminAgents } from "@/features/admin/AdminAgents";
import { RequireAdmin } from "@/shared/auth/RequireAdmin";
import { ChatPage } from "@/features/chat/ChatPage";
import { SearchPage } from "@/features/search/SearchPage";

function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-100 via-white to-blue-50 flex items-center justify-center p-6 font-sans">
      <div className="max-w-md w-full bg-white/80 backdrop-blur-xl shadow-2xl rounded-3xl overflow-hidden border border-white/50">
        <div className="p-8 text-center bg-gradient-to-br from-slate-900 to-slate-800 text-white relative overflow-hidden">
          <div className="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-blue-500 rounded-full opacity-20 blur-xl"></div>
          <div className="absolute bottom-0 left-0 -mb-4 -ml-4 w-24 h-24 bg-indigo-500 rounded-full opacity-20 blur-xl"></div>
          
          <div className="relative inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-white/10 shadow-inner mb-5 backdrop-blur-md border border-white/20">
            <svg className="w-8 h-8 text-blue-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <h1 className="relative text-3xl font-extrabold tracking-tight">OfficeMind AI</h1>
          <p className="relative mt-2 text-slate-300 text-sm font-medium">Your private enterprise intelligence</p>
        </div>
        
        <div className="p-8 space-y-6">
          <AuthPanel />
          <PlatformHealthPanel />
          
          <div className="grid gap-3 pt-6 border-t border-slate-100">
            <Link to="/chat" className="group flex items-center justify-between p-4 rounded-xl bg-blue-600 text-white hover:bg-blue-700 transition-all shadow-md hover:shadow-lg">
              <div className="flex items-center gap-3">
                <svg className="w-5 h-5 text-blue-200" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" /></svg>
                <span className="font-bold">Chat with AI</span>
              </div>
              <span className="opacity-70 group-hover:opacity-100 transition-all transform group-hover:translate-x-1">→</span>
            </Link>
            <Link to="/search" className="group flex items-center justify-between p-4 rounded-xl bg-slate-50 text-slate-800 hover:bg-slate-100 transition-all shadow-sm border border-slate-200 hover:border-slate-300">
              <div className="flex items-center gap-3">
                <svg className="w-5 h-5 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
                <span className="font-semibold">Enterprise Search</span>
              </div>
              <span className="opacity-0 group-hover:opacity-100 transition-all transform group-hover:translate-x-1 text-slate-400">→</span>
            </Link>
            <Link to="/admin" className="group flex items-center justify-between p-4 rounded-xl bg-slate-50 text-slate-600 hover:bg-slate-800 hover:text-white transition-all shadow-sm border border-slate-200">
              <div className="flex items-center gap-3">
                <svg className="w-5 h-5 opacity-70" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                <span className="font-semibold text-sm">Admin Portal</span>
              </div>
              <span className="opacity-0 group-hover:opacity-100 transition-all transform group-hover:translate-x-1 text-slate-400 group-hover:text-white">→</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/search" element={<SearchPage />} />
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
          <Route path="documents" element={<AdminDocuments />} />
          <Route path="ai-settings" element={<AdminAiSettings />} />
          <Route path="agents" element={<AdminAgents />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
