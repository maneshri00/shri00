import React, { useEffect, useState } from "react";
import { fetchUserStatus } from "../services/api.js";

function SummaryHeader({ workspaceType }) {
  const [summary, setSummary] = useState({
    unreadCount: 0,
    upcomingDeadlines: 0,
    highPriorityCount: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    (async () => {
      try {
        const status = await fetchUserStatus();
        const ws = workspaceType;
        if (!status.loggedIn) {
          const sample = ws === "professional"
            ? { unreadCount: 7, upcomingDeadlines: 3, highPriorityCount: 5 }
            : { unreadCount: 4, upcomingDeadlines: 2, highPriorityCount: 3 };
          if (!cancelled) setSummary(sample);
        } else {
          const res = await fetch(
            `http://localhost:8080/summary?workspace_id=${ws}`,
            { credentials: "include" }
          );
          const data = res.ok ? await res.json() : null;
          if (!cancelled) {
            setSummary(
              data || { unreadCount: 0, upcomingDeadlines: 0, highPriorityCount: 0 }
            );
          }
        }
      } catch {
        if (!cancelled) {
          setSummary({ unreadCount: 0, upcomingDeadlines: 0, highPriorityCount: 0 });
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [workspaceType]);

  return (
    <div className="grid grid-cols-3 gap-4">
      <Card title="Unread" value={summary.unreadCount} accent="from-sky-500/40" loading={loading} />
      <Card title="Upcoming" value={summary.upcomingDeadlines} accent="from-amber-500/40" loading={loading} />
      <Card title="High Priority" value={summary.highPriorityCount} accent="from-red-500/40" loading={loading} />
    </div>
  );
}

function Card({ title, value, accent, loading }) {
  return (
    <div className={`p-4 rounded-xl border border-gray-800 bg-gradient-to-br ${accent} to-black`}>
      <div className="text-xs text-gray-400 mb-1">{title}</div>
      <div className="text-2xl font-semibold text-white">
        {loading ? "…" : value}
      </div>
    </div>
  );
}

export default SummaryHeader;

