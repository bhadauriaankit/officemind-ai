import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface AiSettings {
  modelName: string;
  temperature: number;
  systemPrompt: string | null;
  updatedAt: string;
}

function authHeader(token?: string) {
  return { Authorization: `Bearer ${token}` };
}

export function useAdminAiSettings() {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["admin", "ai-settings", token],
    queryFn: async () => {
      const { data } = await httpClient.get<AiSettings>("/ai-settings", {
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
  });
}

export function useUpdateAiSettings() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: { modelName: string; temperature: number; systemPrompt: string }) => {
      const { data } = await httpClient.put<AiSettings>("/ai-settings", input, {
        headers: authHeader(token),
      });
      return data;
    },
    onSuccess: (data) => {
      queryClient.setQueryData(["admin", "ai-settings", token], data);
    },
  });
}
