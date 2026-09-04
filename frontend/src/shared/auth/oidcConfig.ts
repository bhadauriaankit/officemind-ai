import type { AuthProviderProps } from "react-oidc-context";

/**
 * Points at Keycloak's officemind realm. Uses localhost:8081 (not the
 * internal keycloak:8080 hostname) because this runs in the user's
 * browser, not inside the Docker network.
 *
 * automaticSilentRenew: access tokens expire in 5 minutes (Keycloak
 * default). Without this, any request made more than 5 min after login
 * silently fails with a 401 that the UI never surfaces (this was the
 * root cause of the chat bug where only the first, fast message worked).
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
    // strip the OIDC response params (code, state, session_state) from the URL
    // after a successful login redirect, so refreshing doesn't replay it
    window.history.replaceState({}, document.title, window.location.pathname);
  },
};