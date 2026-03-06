import React, { useEffect, useState } from "react";
import { fetchTasks } from "../services/api.js";

function TaskBuckets({ workspaceType }) {
  const [today, setToday] = useState([]);
  const [week, setWeek] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    (async () => {
      try {
        const [t, w] = await Promise.all([
          fetchTasks(workspaceType, "today"),
          fetchTasks(workspaceType, "week"),
        ]);
        if (!cancelled) {
          setToday(Array.isArray(t) ? t : []);
          setWeek(Array.isArray(w) ? w : []);
        }
      } catch {
        if (!cancelled) {
          setToday(sample("today"));
          setWeek(sample("week"));
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [workspaceType]);

  return (
    <div className="grid grid-cols-2 gap-4">
      <Bucket title="Today" items={today} loading={loading} />
      <Bucket title="This Week" items={week} loading={loading} />
    </div>
  );
}

function Bucket({ title, items, loading }) {
  return (
    <div className="p-4 rounded-xl border border-gray-800 bg-black/70">
      <div className="text-sm text-gray-300 mb-2">{title}</div>
      {loading && <div className="text-gray-500">Loading…</div>}
      {!loading && items.length === 0 && (
        <div className="text-gray-500 text-sm">No tasks</div>
      )}
      <div className="space-y-2">
        {items.map((t) => (
          <div key={t.id || t.externalId} className="p-3 rounded-lg bg-black/60 border border-gray-700">
            <div className="text-white text-sm font-semibold">{t.title}</div>
            <div className="text-xs text-gray-400">{t.description}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

function sample(bucket) {
  return [
    { id: `${bucket}-1`, title: bucket === "today" ? "Review inbox" : "Plan sprint", description: "Sample task" },
  ];
}

export default TaskBuckets;

