import React, { useEffect, useState } from "react";
import { fetchNotifications, markNotificationRead } from "../services/api.js";

function NotificationCenter({ workspaceType }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    (async () => {
      try {
        const data = await fetchNotifications(workspaceType);
        if (!cancelled) setItems(Array.isArray(data) ? data : []);
      } catch {
        if (!cancelled) setItems(sample());
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [workspaceType]);

  const onRead = async (id) => {
    try {
      await markNotificationRead(id);
      setItems((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
    } catch {}
  };

  return (
    <div className="p-4 rounded-xl border border-gray-800 bg-black/70">
      <div className="text-sm text-gray-300 mb-2">Notification Center</div>
      {loading && <div className="text-gray-500">Loading…</div>}
      {!loading && items.length === 0 && (
        <div className="text-gray-500 text-sm">No notifications</div>
      )}
      <div className="space-y-2">
        {items.map((n) => (
          <div key={n.id} className="p-3 rounded-lg bg-black/60 border border-gray-700 flex justify-between items-center">
            <div>
              <div className="text-white text-sm font-semibold">{n.title}</div>
              <div className="text-xs text-gray-400">{n.message}</div>
            </div>
            <button
              onClick={() => onRead(n.id)}
              disabled={n.read}
              className={`
                px-3 py-1 rounded-lg text-xs
                ${n.read ? 'bg-gray-700 text-gray-300 cursor-not-allowed' : 'bg-sky-600 text-white hover:bg-sky-500'}
              `}
            >
              {n.read ? 'Read' : 'Mark read'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

function sample() {
  return [
    { id: "n1", title: "Reminder", message: "Standup in 30 minutes", read: false },
    { id: "n2", title: "Deadline", message: "Report due tomorrow", read: false },
  ];
}

export default NotificationCenter;

