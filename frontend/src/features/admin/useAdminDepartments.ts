import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface Department {
  id: string;
  name: string;
  description: string | null;
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

export function useAdminDepartments(page = 0, size = 20) {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["admin", "departments", page, size, token],
    queryFn: async () => {
      const { data } = await httpClient.get<PageResponse<Department>>("/departments", {
        params: { page, size },
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
  });
}

export function useCreateDepartment() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: { name: string; description: string }) => {
      const { data } = await httpClient.post<Department>("/departments", input, {
        headers: authHeader(token),
      });
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "departments"] });
    },
  });
}

export function useDeleteDepartment() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await httpClient.delete(`/departments/${id}`, { headers: authHeader(token) });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "departments"] });
    },
  });
}
