import type { AuthProviderProps } from "react-oidc-context";

/**
 * Points at Keycloak's officemind realm.
 * 
 * LOGOUT FIX: Use signoutRedirect() (not removeUser()) to fully end the
 * Keycloak SSO session. removeUser() only clears local state — the browser
 * still has a Keycloak session cookie so it silently re-authenticates.
 * signoutRedirect() hits Keycloak's /logout endpoint, clearing the SSO
 * cookie before redirecting back to the app.
 */
export const oidcConfig: AuthProviderProps = {
  authority: "http://localhost:8081/realms/officemind",
  client_id: "officemind-frontend",
  redirect_uri: window.location.origin,
  post_logout_redirect_uri: window.location.origin,
  scope: "openid profile email",
  automaticSilentRenew: true,
  silent_redirect_uri: `${window.location.origin}/silent-renew.html`,
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname);
  },
};
