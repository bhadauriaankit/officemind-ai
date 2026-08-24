import { PlatformHealthPanel } from "@/features/system/PlatformHealthPanel";
import { AuthPanel } from "@/features/system/AuthPanel";

export function App() {
  return (
    <div className="min-h-screen bg-slate-50 p-8">
      <header className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">OfficeMind AI</h1>
        <p className="text-sm text-slate-500">
          Private enterprise AI assistant
        </p>
      </header>
      <main className="max-w-md space-y-6">
        <AuthPanel />
        <PlatformHealthPanel />
      </main>
    </div>
  );
}
