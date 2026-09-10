import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface Notification {
  id: string;
  type: string;
  title: string;
  body: string;
  read: boolean;
  createdAt: string;
}

function authHeader(token?: string) {
  return { Authorization: `Bearer ${token}` };
}

export function useNotifications() {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["notifications", token],
    queryFn: async () => {
      const { data } = await httpClient.get<Notification[]>("/notifications?unreadOnly=false", {
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
    refetchInterval: 15_000,
    staleTime: 10_000,
  });
}

export function useUnreadCount() {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["notifications", "unread", token],
    queryFn: async () => {
      const { data } = await httpClient.get<Notification[]>("/notifications?unreadOnly=true", {
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
    refetchInterval: 15_000,
    staleTime: 10_000,
    select: (data) => data.length,
  });
}

export function useMarkRead() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await httpClient.patch(`/notifications/${id}/read`, {}, {
        headers: authHeader(token),
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });
}
