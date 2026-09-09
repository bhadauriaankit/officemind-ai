import { useQuery } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface Agent {
  id: string;
  key: string;
  name: string;
  description: string | null;
  systemPrompt: string;
  createdAt: string;
  updatedAt: string;
}

function authHeader(token?: string) {
  return { Authorization: `Bearer ${token}` };
}

// GET /agents is open to any authenticated user (not admin-gated) --
// used both by the admin management page and the chat agent picker.
export function useAgents() {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["agents", token],
    queryFn: async () => {
      const { data } = await httpClient.get<Agent[]>("/agents", {
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
  });
}
