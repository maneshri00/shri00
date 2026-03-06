import React, { useEffect, useState } from "react";
import { executeCommand, fetchUserStatus } from "../services/api.js";

function CommandList({ commands = [] }) {
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const status = await fetchUserStatus();
      if (!cancelled) setLoggedIn(!!status.loggedIn);
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const run = async (cmd) => {
    try {
      await executeCommand(cmd.command_id);
    } catch (e) {}
  };

  if (!commands.length) {
    return <div className="text-gray-400">No commands available</div>;
  }

  return (
    <div className="space-y-3">
      {commands.map((c) => (
        <div
          key={c.command_id}
          className="
            p-4 rounded-xl
            bg-black/70 border border-sky-400/40
            hover:border-sky-300 transition
          "
        >
          <div className="flex items-center justify-between">
            <div>
              <div className="text-white font-semibold">{c.label}</div>
              <div className="text-xs text-gray-400">
                {c.target_platform} • {c.payload_summary}
              </div>
            </div>
            <button
              onClick={() => run(c)}
              disabled={!loggedIn}
              className="
                px-3 py-1 rounded-lg
                text-white text-sm transition
                ${!loggedIn ? 'bg-gray-700 cursor-not-allowed' : 'bg-sky-600 hover:bg-sky-500'}
              "
            >
              {loggedIn ? 'Execute' : 'Login to execute'}
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}

export default CommandList;
