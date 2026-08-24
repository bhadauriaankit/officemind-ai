import { useAuth } from "react-oidc-context";
import { useCurrentUser } from "./useCurrentUser";

export function AuthPanel() {
  const auth = useAuth();

  if (auth.isLoading) {
    return <p className="text-sm text-slate-500">Loading authentication…</p>;
  }

  if (auth.error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
        Authentication error: {auth.error.message}
      </div>
    );
  }

  if (!auth.isAuthenticated) {
    return (
      <div className="rounded-lg border border-slate-200 p-4 shadow-sm">
        <p className="mb-3 text-sm text-slate-600">You're not signed in.</p>
        <button
          onClick={() => auth.signinRedirect()}
          className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          Sign in
        </button>
      </div>
    );
  }

  return <SignedInPanel onSignOut={() => auth.removeUser()} />;
}

function SignedInPanel({ onSignOut }: { onSignOut: () => void }) {
  const { data: profile, isLoading, isError } = useCurrentUser();

  return (
    <div className="rounded-lg border border-slate-200 p-4 shadow-sm">
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-base font-semibold text-slate-800">Your Profile</h2>
        <button
          onClick={onSignOut}
          className="text-sm font-medium text-slate-500 hover:text-slate-800"
        >
          Sign out
        </button>
      </div>

      {isLoading && <p className="text-sm text-slate-500">Loading profile…</p>}
      {isError && <p className="text-sm text-red-600">Couldn't load your profile.</p>}

      {profile && (
        <dl className="space-y-1 text-sm">
          <div className="flex justify-between">
            <dt className="text-slate-500">Name</dt>
            <dd className="text-slate-800">{profile.displayName}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-slate-500">Email</dt>
            <dd className="text-slate-800">{profile.email}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-slate-500">Roles</dt>
            <dd className="text-slate-800">{profile.roles.join(", ")}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-slate-500">Status</dt>
            <dd className="text-slate-800">{profile.status}</dd>
          </div>
        </dl>
      )}
    </div>
  );
}
