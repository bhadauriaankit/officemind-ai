import { useQuery } from "@tanstack/react-query";
import { useAuth } from "react-oidc-context";
import { httpClient } from "@/shared/api/httpClient";

export interface UserProfile {
  id: string;
  email: string;
  displayName: string;
  department: string | null;
  roles: string[];
  status: string;
  createdAt: string;
  updatedAt: string;
}

async function fetchCurrentUser(accessToken: string): Promise<UserProfile> {
  const { data } = await httpClient.get<UserProfile>("/users/me", {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  return data;
}

export function useCurrentUser() {
  const auth = useAuth();
  const accessToken = auth.user?.access_token;

  return useQuery({
    queryKey: ["users", "me", accessToken],
    queryFn: () => fetchCurrentUser(accessToken!),
    enabled: !!accessToken,
    retry: 1,
  });
}
