import { PlatformHealthPanel } from "@/features/system/PlatformHealthPanel";

export function AdminDashboard() {
  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-slate-900">Dashboard</h2>
      <div className="max-w-md">
        <PlatformHealthPanel />
      </div>
    </div>
  );
}
