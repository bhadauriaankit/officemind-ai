import { ChangeEvent, useRef, useState } from "react";
import {
  AdminDocument,
  useAdminDocuments,
  useUploadDocument,
  useDeleteDocument,
  useDownloadDocument,
} from "./useAdminDocuments";

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

const STATUS_STYLES: Record<AdminDocument["status"], string> = {
  UPLOADED: "bg-slate-100 text-slate-600",
  PROCESSING: "bg-amber-100 text-amber-700",
  READY: "bg-emerald-100 text-emerald-700",
  FAILED: "bg-red-100 text-red-700",
};

export function AdminDocuments() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useAdminDocuments(page, 20);
  const upload = useUploadDocument();
  const deleteDoc = useDeleteDocument();
  const download = useDownloadDocument();
  const fileInputRef = useRef<HTMLInputElement>(null);

  function handleFileSelect(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    upload.mutate(file);
    e.target.value = ""; // allow re-selecting the same file later
  }

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold text-slate-900">Documents</h2>

      <div className="rounded-lg border border-slate-200 bg-white p-4">
        <input
          ref={fileInputRef}
          type="file"
          onChange={handleFileSelect}
          className="hidden"
        />
        <button
          onClick={() => fileInputRef.current?.click()}
          disabled={upload.isPending}
          className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-40"
        >
          {upload.isPending ? "Uploading…" : "Upload document"}
        </button>
        {upload.isError && (
          <p className="mt-2 text-sm text-red-600">Upload failed. Try again.</p>
        )}
      </div>

      {isLoading && <p className="text-sm text-slate-500">Loading documents…</p>}
      {isError && <p className="text-sm text-red-600">Couldn't load documents.</p>}

      {data && (
        <>
          <div className="overflow-hidden rounded-lg border border-slate-200 bg-white">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-200 bg-slate-50 text-xs uppercase text-slate-500">
                <tr>
                  <th className="px-4 py-3">File name</th>
                  <th className="px-4 py-3">Size</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3">Uploaded</th>
                  <th className="px-4 py-3">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.items.map((doc) => (
                  <tr key={doc.id}>
                    <td className="px-4 py-3 font-medium text-slate-800">{doc.fileName}</td>
                    <td className="px-4 py-3 text-slate-500">{formatBytes(doc.sizeBytes)}</td>
                    <td className="px-4 py-3">
                      <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${STATUS_STYLES[doc.status]}`}>
                        {doc.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-500">
                      {new Date(doc.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-3">
                        <button
                          onClick={() => download(doc)}
                          className="text-xs font-medium text-slate-600 hover:text-slate-900"
                        >
                          Download
                        </button>
                        <button
                          disabled={deleteDoc.isPending}
                          onClick={() => deleteDoc.mutate(doc.id)}
                          className="text-xs font-medium text-red-600 hover:text-red-800 disabled:opacity-40"
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
                {data.items.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-4 py-6 text-center text-slate-400">
                      No documents yet.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="flex items-center justify-between text-sm text-slate-500">
            <span>
              Page {data.page + 1} of {Math.max(data.totalPages, 1)} — {data.totalElements} total
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => setPage((p) => Math.max(p - 1, 0))}
                disabled={data.page === 0}
                className="rounded-md border border-slate-300 px-3 py-1 disabled:opacity-40"
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={data.page + 1 >= data.totalPages}
                className="rounded-md border border-slate-300 px-3 py-1 disabled:opacity-40"
              >
                Next
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
