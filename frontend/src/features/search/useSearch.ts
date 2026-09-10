import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { httpClient } from "@/shared/api/httpClient";

export interface SearchResult {
  chunkId: string;
  documentId: string;
  documentFileName: string;
  chunkIndex: number;
  content: string;
  score: number;
}

async function searchDocuments(query: string, limit: number): Promise<SearchResult[]> {
  const { data } = await httpClient.post<SearchResult[]>("/search", { query, limit });
  return data;
}

export function useSearch() {
  const [query, setQuery] = useState("");
  const [limit, setLimit] = useState(10);

  const mutation = useMutation({
    mutationFn: () => searchDocuments(query.trim(), limit),
  });

  return {
    query,
    setQuery,
    limit,
    setLimit,
    results: mutation.data ?? [],
    isSearching: mutation.isPending,
    isError: mutation.isError,
    error: mutation.error,
    search: () => {
      if (query.trim()) mutation.mutate();
    },
    reset: () => mutation.reset(),
  };
}
