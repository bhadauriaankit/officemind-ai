import { ChangeEvent, useRef, useState } from "react";
import {
  AdminDocument,
  useAdminDocuments,
  useUploadDocument,
  useDeleteDocument,
  useDownloadDocument,
  useReindexDocument,
} from "./useAdminDocuments";

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

const STATUS_STYLES: Record<AdminDocument["status"], { bg: string; text: string; dot: string }> = {
  UPLOADED: { bg: "bg-blue-50", text: "text-blue-700", dot: "bg-blue-500" },
  PROCESSING: { bg: "bg-amber-50", text: "text-amber-700", dot: "bg-amber-500 animate-pulse" },
  READY: { bg: "bg-emerald-50", text: "text-emerald-700", dot: "bg-emerald-500" },
  FAILED: { bg: "bg-red-50", text: "text-red-700", dot: "bg-red-500" },
};

export function AdminDocuments() {
  const [page, setPage] = useState(0);
  const [deletingDoc, setDeletingDoc] = useState<AdminDocument | null>(null);
  const { data, isLoading, isError } = useAdminDocuments(page, 20);
  const upload = useUploadDocument();
  const deleteDoc = useDeleteDocument();
  const download = useDownloadDocument();
  const reindex = useReindexDocument();
  const fileInputRef = useRef<HTMLInputElement>(null);

  function handleFileSelect(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    upload.mutate(file);
    e.target.value = "";
  }

  function confirmDelete() {
    if (!deletingDoc) return;
    deleteDoc.mutate(deletingDoc.id, {
      onSuccess: () => setDeletingDoc(null),
    });
  }

  return (
    <div className="space-y-6">
      {/* Delete Confirmation Modal */}
      {deletingDoc && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
          <div className="w-full max-w-sm rounded-2xl bg-white shadow-2xl border border-slate-200 p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
                <svg className="w-5 h-5 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </div>
              <div>
                <h3 className="text-base font-bold text-slate-900">Delete Document</h3>
                <p className="text-xs text-slate-500">Irreversible action</p>
              </div>
            </div>
            <p className="text-sm text-slate-600 mb-5">
              Are you sure you want to delete <span className="font-semibold text-slate-800">"{deletingDoc.fileName}"</span>? Its indexed vectors will also be removed.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setDeletingDoc(null)}
                className="flex-1 rounded-xl border border-slate-200 px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={confirmDelete}
                disabled={deleteDoc.isPending}
                className="flex-1 rounded-xl bg-red-600 px-4 py-2.5 text-sm font-bold text-white hover:bg-red-700 disabled:opacity-50 transition-colors"
              >
                {deleteDoc.isPending ? "Deleting…" : "Delete"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Header with Upload */}
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Knowledge Base Documents</h2>
          <p className="text-sm text-slate-500 mt-1">Upload company policies, guides, and manuals for AI indexing.</p>
        </div>
        <div>
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileSelect}
            className="hidden"
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={upload.isPending}
            className="flex items-center gap-2 rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-bold text-white hover:bg-slate-700 shadow-sm transition-colors disabled:opacity-40"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
            </svg>
            {upload.isPending ? "Uploading…" : "Upload Document"}
          </button>
        </div>
      </div>

      {upload.isError && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700 font-medium">
          Upload failed. Check file size and format, then try again.
        </div>
      )}

      {isLoading && (
        <div className="flex items-center justify-center py-16 text-slate-400 gap-3">
          <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
          Loading documents…
        </div>
      )}

      {isError && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700 font-medium">
          Failed to load documents. Check your connection or permissions.
        </div>
      )}

      {data && (
        <>
          <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-100 bg-slate-50 text-xs font-bold uppercase tracking-wider text-slate-500">
                <tr>
                  <th className="px-5 py-3.5">Document</th>
                  <th className="px-5 py-3.5">Size</th>
                  <th className="px-5 py-3.5">Status</th>
                  <th className="px-5 py-3.5">Uploaded</th>
                  <th className="px-5 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.items.map((doc) => {
                  const style = STATUS_STYLES[doc.status] || STATUS_STYLES.UPLOADED;
                  return (
                    <tr key={doc.id} className="hover:bg-slate-50/60 transition-colors">
                      <td className="px-5 py-4 font-semibold text-slate-800">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center shrink-0">
                            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                          </div>
                          <span className="truncate max-w-xs">{doc.fileName}</span>
                        </div>
                      </td>
                      <td className="px-5 py-4 text-slate-500 font-medium">{formatBytes(doc.sizeBytes)}</td>
                      <td className="px-5 py-4">
                        <span className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-bold ${style.bg} ${style.text}`}>
                          <span className={`w-1.5 h-1.5 rounded-full ${style.dot}`}></span>
                          {doc.status}
                        </span>
                      </td>
                      <td className="px-5 py-4 text-slate-500 text-xs">
                        {new Date(doc.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}
                      </td>
                      <td className="px-5 py-4">
                        <div className="flex items-center justify-end gap-2">
                          {(doc.status === "FAILED" || doc.status === "UPLOADED") && (
                            <button
                              onClick={() => reindex.mutate(doc.id)}
                              disabled={reindex.isPending}
                              className="rounded-lg border border-blue-200 bg-blue-50 px-2.5 py-1.5 text-xs font-bold text-blue-700 hover:bg-blue-100 transition-colors disabled:opacity-40"
                              title="Trigger indexing pipeline"
                            >
                              Retry Indexing
                            </button>
                          )}
                          <button
                            onClick={() => download(doc)}
                            className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-100 hover:border-slate-300 transition-colors"
                          >
                            Download
                          </button>
                          <button
                            onClick={() => setDeletingDoc(doc)}
                            className="rounded-lg border border-red-200 px-3 py-1.5 text-xs font-semibold text-red-600 hover:bg-red-50 transition-colors"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
                {data.items.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-5 py-12 text-center text-sm text-slate-400">
                      No documents uploaded yet. Click "Upload Document" to get started.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {data.totalPages > 1 && (
            <div className="flex items-center justify-between text-sm text-slate-500">
              <span>Page {data.page + 1} of {data.totalPages} — {data.totalElements} documents total</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(p - 1, 0))}
                  disabled={data.page === 0}
                  className="rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold disabled:opacity-40 hover:bg-slate-50 transition-colors"
                >
                  ← Previous
                </button>
                <button
                  onClick={() => setPage((p) => p + 1)}
                  disabled={data.page + 1 >= data.totalPages}
                  className="rounded-xl border border-slate-200 px-4 py-2 text-xs font-semibold disabled:opacity-40 hover:bg-slate-50 transition-colors"
                >
                  Next →
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
