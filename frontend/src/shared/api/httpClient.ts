import axios from "axios";

/**
 * Central Axios instance. Auth token attachment (Keycloak-issued JWT) is
 * wired here in Phase 2 via an interceptor; kept bare in Phase 1 so the
 * platform health check can be exercised without auth in place yet.
 */
export const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api/v1",
  timeout: 10_000,
  headers: {
    "Content-Type": "application/json",
  },
});

httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Centralized error normalization; expanded once ProblemDetail responses
    // land from the backend's @ControllerAdvice (added alongside Phase 2 auth).
    return Promise.reject(error);
  }
);
