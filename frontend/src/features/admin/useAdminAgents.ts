import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";
import { Agent } from "@/features/agents/useAgents";

function authHeader(token?: string) {
  return { Authorization: `Bearer ${token}` };
}

export interface CreateAgentInput {
  key: string;
  name: string;
  description: string;
  systemPrompt: string;
}

export interface UpdateAgentInput {
  id: string;
  name: string;
  description: string;
  systemPrompt: string;
}

export function useCreateAgent() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: CreateAgentInput) => {
      const { data } = await httpClient.post<Agent>("/agents", input, {
        headers: authHeader(token),
      });
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
    },
  });
}

export function useUpdateAgent() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, ...input }: UpdateAgentInput) => {
      const { data } = await httpClient.put<Agent>(`/agents/${id}`, input, {
        headers: authHeader(token),
      });
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
    },
  });
}

export function useDeleteAgent() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await httpClient.delete(`/agents/${id}`, { headers: authHeader(token) });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
    },
  });
}
