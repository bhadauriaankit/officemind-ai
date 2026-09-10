import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { httpClient } from "@/shared/api/httpClient";

export interface Notification {
  id: string;
  type: string;
  title: string;
  body: string;
  read: boolean;
  createdAt: string;
}

async function fetchNotifications(unreadOnly: boolean): Promise<Notification[]> {
  const { data } = await httpClient.get<Notification[]>(`/notifications?unreadOnly=${unreadOnly}`);
  return data;
}

async function markRead(id: string): Promise<void> {
  await httpClient.patch(`/notifications/${id}/read`);
}

export function useNotifications() {
  return useQuery({
    queryKey: ["notifications"],
    queryFn: () => fetchNotifications(false),
    refetchInterval: 15_000, // poll every 15s for new notifications
    staleTime: 10_000,
  });
}

export function useUnreadCount() {
  return useQuery({
    queryKey: ["notifications", "unread"],
    queryFn: () => fetchNotifications(true),
    refetchInterval: 15_000,
    staleTime: 10_000,
    select: (data) => data.length,
  });
}

export function useMarkRead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: markRead,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });
}
