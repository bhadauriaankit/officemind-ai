import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface AdminDocument {
  id: string;
  fileName: string;
  contentType: string | null;
  sizeBytes: number;
  version: number;
  status: "UPLOADED" | "PROCESSING" | "READY" | "FAILED";
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

export function useAdminDocuments(page = 0, size = 20) {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return useQuery({
    queryKey: ["admin", "documents", page, size, token],
    queryFn: async () => {
      const { data } = await httpClient.get<PageResponse<AdminDocument>>("/documents", {
        params: { page, size },
        headers: authHeader(token),
      });
      return data;
    },
    enabled: !!token,
  });
}

export function useUploadDocument() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (file: File) => {
      const formData = new FormData();
      formData.append("file", file);
      const { data } = await httpClient.post<AdminDocument>("/documents", formData, {
        headers: { ...authHeader(token), "Content-Type": "multipart/form-data" },
      });
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "documents"] });
    },
  });
}

export function useDeleteDocument() {
  const auth = useAuth();
  const token = auth.user?.access_token;
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await httpClient.delete(`/documents/${id}`, { headers: authHeader(token) });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "documents"] });
    },
  });
}

export function useDownloadDocument() {
  const auth = useAuth();
  const token = auth.user?.access_token;

  return async (doc: AdminDocument) => {
    const response = await httpClient.get(`/documents/${doc.id}/download`, {
      headers: authHeader(token),
      responseType: "blob",
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.download = doc.fileName;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  };
}
