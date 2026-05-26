import { AppStatus, APP_STATUSES, statusLabels, statusTone } from "@/lib/constants";

export function StatusPill({ status }: { status: string }) {
  const safeStatus: AppStatus = APP_STATUSES.includes(status as AppStatus)
    ? (status as AppStatus)
    : "DEVELOPING";

  return (
    <span className={`rounded-md border px-2 py-1 text-xs ${statusTone[safeStatus]}`}>
      {statusLabels[safeStatus]}
    </span>
  );
}
