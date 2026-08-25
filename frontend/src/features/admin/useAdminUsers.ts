import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface AdminUser {
  id: string;
  email: string;
  displayName: string;
  department: string | null;
  roles: string[];
  status: "ACTIVE" | "DISABLED";
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

export function useAdminUsers(page = 0, size = 20) {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["admin", "users", page, size, token],
    queryFn: async () => {
      const { data } = await httpClient.get<PageResponse<AdminUser>>("/users", {
        params: { page, size },
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
  });
}

export function useSetUserStatus() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ userId, action }: { userId: string; action: "disable" | "reactivate" }) => {
      const { data } = await httpClient.post<AdminUser>(
        `/users/${userId}/${action}`,
        {},
        { headers: authHeader(token) }
      );
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });
}

export function useUpdateUserRoles() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ userId, roles }: { userId: string; roles: string[] }) => {
      const { data } = await httpClient.patch<AdminUser>(
        `/users/${userId}/roles`,
        { roles },
        { headers: authHeader(token) }
      );
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "users"] });
    },
  });
}
