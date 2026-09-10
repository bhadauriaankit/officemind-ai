import { ReactNode } from "react";
import { useAuth } from "react-oidc-context";
import { Link } from "react-router-dom";

export function RequireAuth({ children }: { children: ReactNode }) {
  const auth = useAuth();

  if (auth.isLoading) {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-50">
        <div className="flex items-center gap-3 text-slate-500 font-medium text-sm">
          <svg className="w-5 h-5 animate-spin text-blue-600" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Checking authentication session…
        </div>
      </div>
    );
  }

  if (!auth.isAuthenticated) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-100 via-white to-blue-50 flex items-center justify-center p-6 font-sans">
        <div className="max-w-md w-full bg-white/90 backdrop-blur-xl p-8 rounded-3xl shadow-xl border border-slate-200 text-center space-y-6">
          <div className="w-16 h-16 bg-blue-50 text-blue-600 rounded-2xl flex items-center justify-center mx-auto shadow-inner border border-blue-100">
            <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <div>
            <h2 className="text-2xl font-extrabold text-slate-900">Sign In to Continue</h2>
            <p className="text-sm text-slate-500 mt-2">
              You need an active session to chat with OfficeMind AI and search company documentation.
            </p>
          </div>
          <div className="rounded-2xl bg-slate-50 border border-slate-200 p-4 text-left text-xs text-slate-600 space-y-1.5">
            <p className="font-bold text-slate-700 uppercase tracking-wider text-[10px]">Test Account Credentials:</p>
            <div className="flex justify-between font-mono">
              <span className="text-slate-500">Username:</span>
              <span className="font-bold text-slate-800">admin.user</span>
            </div>
            <div className="flex justify-between font-mono">
              <span className="text-slate-500">Password:</span>
              <span className="font-bold text-slate-800">Admin123!</span>
            </div>
          </div>
          <button
            onClick={() => auth.signinRedirect()}
            className="w-full rounded-xl bg-blue-600 px-5 py-3.5 text-sm font-bold text-white shadow-md hover:bg-blue-700 transition-all active:scale-98"
          >
            Sign In with Keycloak
          </button>
          <div>
            <Link to="/" className="text-xs font-semibold text-slate-400 hover:text-slate-600 transition-colors">
              ← Return to Home
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}
