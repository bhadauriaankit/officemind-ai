import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface ChatMessage {
  role: "USER" | "ASSISTANT";
  content: string;
  sentAt: string;
}

export interface Conversation {
  id: string;
  agentId: string | null;
  title: string;
  messages: ChatMessage[];
  createdAt: string;
  updatedAt: string;
}

interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

function authHeader(token?: string) {
  return { Authorization: `Bearer ${token}` };
}

export function useConversationList() {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["conversations", token],
    queryFn: async () => {
      const { data } = await httpClient.get<PageResponse<Conversation>>("/conversations", {
        params: { page: 0, size: 50 },
        headers: authHeader(token),
      });
      return data.items;
    },
    enabled: !!token,
  });
}

export function useConversation(id: string | null) {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["conversation", id, token],
    queryFn: async () => {
      const { data } = await httpClient.get<Conversation>(`/conversations/${id}`, {
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token && !!id,
  });
}

export function useStartConversation() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ message, agentId }: { message: string; agentId?: string | null }) => {
      const { data } = await httpClient.post<Conversation>(
        "/conversations",
        { message, agentId: agentId || null },
        { headers: authHeader(token) }
      );
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["conversations"] });
    },
    onError: (err) => {
      console.error("startConversation failed:", err);
    },
  });
}

export function useSendMessage() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ conversationId, message }: { conversationId: string; message: string }) => {
      const { data } = await httpClient.post<Conversation>(
        `/conversations/${conversationId}/messages`,
        { message },
        { headers: authHeader(token) }
      );
      return data;
    },
    onSuccess: (data) => {
      queryClient.setQueryData(["conversation", data.id, token], data);
      queryClient.invalidateQueries({ queryKey: ["conversations"] });
    },
    onError: (err) => {
      console.error("sendMessage failed:", err);
    },
  });
}

/** DELETE /conversations/{id} — permanently removes a conversation from history */
export function useDeleteConversation() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (conversationId: string) => {
      await httpClient.delete(`/conversations/${conversationId}`, {
        headers: authHeader(token),
      });
      return conversationId;
    },
    onSuccess: (deletedId) => {
      // Remove from list cache immediately (optimistic)
      queryClient.setQueryData<Conversation[]>(["conversations", token], (old) =>
        old ? old.filter((c) => c.id !== deletedId) : []
      );
      queryClient.removeQueries({ queryKey: ["conversation", deletedId] });
    },
    onError: (err) => {
      console.error("deleteConversation failed:", err);
    },
  });
}

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any;
  const status = anyErr?.response?.status;
  const backendMessage = anyErr?.response?.data?.message;
  if (status === 401) return "Your session expired. Please refresh the page and sign in again.";
  if (backendMessage) return backendMessage;
  if (anyErr?.message === "Network Error") return "Couldn't reach the server. Check that the backend is running.";
  return "Something went wrong. Please try again.";
}

export { extractErrorMessage };
