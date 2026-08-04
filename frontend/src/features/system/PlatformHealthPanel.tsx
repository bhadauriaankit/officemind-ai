import { usePlatformHealth } from "./usePlatformHealth";

export function PlatformHealthPanel() {
  const { data, isLoading, isError } = usePlatformHealth();

  if (isLoading) {
    return <p className="text-sm text-slate-500">Checking platform status…</p>;
  }

  if (isError || !data) {
    return (
      <p className="text-sm text-red-600">
        Unable to reach the OfficeMind AI backend.
      </p>
    );
  }

  return (
    <div className="rounded-lg border border-slate-200 p-4 shadow-sm">
      <div className="mb-3 flex items-center gap-2">
        <span
          className={`h-2.5 w-2.5 rounded-full ${
            data.healthy ? "bg-emerald-500" : "bg-red-500"
          }`}
        />
        <h2 className="text-base font-semibold text-slate-800">
          Platform Status: {data.healthy ? "Healthy" : "Degraded"}
        </h2>
      </div>
      <ul className="space-y-1">
        {data.components.map((c) => (
          <li key={c.component} className="flex justify-between text-sm">
            <span className="capitalize text-slate-600">{c.component}</span>
            <span className={c.healthy ? "text-emerald-600" : "text-red-600"}>
              {c.healthy ? "OK" : "DOWN"}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
