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
    mutationFn: async (message: string) => {
      const { data } = await httpClient.post<Conversation>(
        "/conversations",
        { message },
        { headers: authHeader(token) }
      );
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["conversations"] });
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
  });
}
