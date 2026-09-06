import axios from "axios";

/**
 * Central Axios instance. Auth token attachment (Keycloak-issued JWT) is
 * wired here in Phase 2 via an interceptor; kept bare in Phase 1 so the
 * platform health check can be exercised without auth in place yet.
 */
export const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api/v1",
  // 10s was fine pre-Phase-6, but RAG-augmented chat responses (embed
  // query -> Qdrant search -> LLM generation, all sequential) can
  // legitimately take longer, especially on a cold model load. 90s
  // headroom below the backend's own 120s Ollama read timeout so a
  // genuine backend timeout still surfaces as a real error rather than
  // the frontend giving up first and masking it.
  timeout: 90_000,
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
