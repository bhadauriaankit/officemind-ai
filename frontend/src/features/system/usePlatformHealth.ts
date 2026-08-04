import { useQuery } from "@tanstack/react-query";
import { httpClient } from "@/shared/api/httpClient";

export interface ComponentStatus {
  component: string;
  healthy: boolean;
  detail: string;
}

export interface PlatformHealthReport {
  healthy: boolean;
  components: ComponentStatus[];
}

async function fetchPlatformHealth(): Promise<PlatformHealthReport> {
  const { data } = await httpClient.get<PlatformHealthReport>("/system/health");
  return data;
}

export function usePlatformHealth() {
  return useQuery({
    queryKey: ["system", "health"],
    queryFn: fetchPlatformHealth,
    refetchInterval: 15_000,
    retry: 1,
  });
}
