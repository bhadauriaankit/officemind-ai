import { ReactNode } from "react";
import { useAuth } from "react-oidc-context";
import { decodeJwtPayload } from "./decodeJwt";

/**
 * Gates admin-only UI. Checks the ADMIN realm role off the ACCESS token
 * (not the ID token / auth.user.profile — Keycloak's realm_access claim
 * only appears on the access token). The backend's @PreAuthorize checks
 * remain the real enforcement layer; this is purely a UX guard.
 */
export function RequireAdmin({ children }: { children: ReactNode }) {
  const auth = useAuth();

  if (auth.isLoading) {
    return <p className="p-8 text-sm text-slate-500">Loading…</p>;
  }

  if (!auth.isAuthenticated) {
    return (
      <div className="p-8">
        <p className="mb-3 text-sm text-slate-600">You need to sign in to view this page.</p>
        <button
          onClick={() => auth.signinRedirect()}
          className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          Sign in
        </button>
      </div>
    );
  }

  const accessToken = auth.user?.access_token;
  const payload = accessToken
    ? decodeJwtPayload<{ realm_access?: { roles?: string[] } }>(accessToken)
    : null;
  const roles = payload?.realm_access?.roles ?? [];
  const isAdmin = roles.includes("ADMIN");

  console.log("DEBUG accessToken present:", !!accessToken);
  console.log("DEBUG payload:", payload);
  console.log("DEBUG roles:", roles);

  if (!isAdmin) {
    return (
      <div className="p-8">
        <p className="text-sm text-red-600">
          You don't have permission to view this page. (Admin role required.)
        </p>
      </div>
    );
  }

  return <>{children}</>;
}
