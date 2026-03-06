import React from "react";

function ImportantMessageList({ messages = [] }) {
  if (!messages.length) {
    return <div className="text-gray-400">No important messages</div>;
  }

  return (
    <div className="space-y-3">
      {messages.map((m) => (
        <div
          key={m.id || `${m.source_platform}-${m.subject}`}
          className="
            p-4 rounded-xl
            bg-black/70 border border-emerald-400/40
            hover:border-emerald-300 transition
          "
        >
          <div className="flex items-start justify-between gap-3">
            <div className="flex-1">
              <div className="text-sm text-gray-400">
                {m.source_platform} • {m.sender}
              </div>
              <div className="text-white font-semibold">{m.subject}</div>
              <div className="text-xs text-gray-300 mt-1">{m.preview}</div>
            </div>
            <div className="text-xs text-gray-400">
              {formatTime(m.received_at)}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

function formatTime(ts) {
  try {
    const d = new Date(ts);
    return d.toLocaleString();
  } catch {
    return "";
  }
}

export default ImportantMessageList;

