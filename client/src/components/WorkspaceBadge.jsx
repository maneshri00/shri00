import React, { useEffect, useState } from "react";
import { fetchUserStatus } from "../services/api.js";

function WorkspaceBadge({ initial }) {
  const [ws, setWs] = useState(initial || (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) || null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const s = await fetchUserStatus();
      if (!cancelled && s.workspaceAllowed) setWs(s.workspaceAllowed);
    })();
    return () => { cancelled = true; };
  }, []);

  if (!ws) return null;
  return (
    <div className="px-3 py-1 rounded-lg text-xs bg-gray-800 text-white inline-flex items-center gap-2">
      <span>Workspace</span>
      <span className="font-semibold">{ws}</span>
    </div>
  );
}

export default WorkspaceBadge;

