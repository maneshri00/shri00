import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchUserStatus } from "../services/api.js";

function SwitchWorkspace() {
  const navigate = useNavigate();
  const [status, setStatus] = useState({ loggedIn: false, workspaceAllowed: null });
  const [target, setTarget] = useState(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const s = await fetchUserStatus();
      if (!cancelled) setStatus(s);
    })();
    return () => { cancelled = true; };
  }, []);

  const startSwitch = async () => {
    const base = import.meta.env.VITE_API_BASE || "http://localhost:8080";
    const ws = target;
    try {
      await fetch(`${base}/api/auth/logout`, { method: "POST", credentials: "include" });
    } catch {}
    window.location.href = `${base}/auth/google?workspace=${ws}`;
  };

  return (
    <div className="min-h-screen bg-black text-white p-6">
      <div className="flex items-center justify-between mb-6">
        <div className="text-xl font-semibold">Switch Workspace</div>
        <button onClick={() => navigate(-1)} className="px-3 py-1 rounded-lg bg-gray-800 text-white text-sm">Back</button>
      </div>
      <div className="p-4 border border-gray-800 rounded-xl bg-black/70 max-w-xl">
        <div className="text-sm text-gray-300 mb-3">
          Current: {status.workspaceAllowed || "none"} {status.loggedIn ? "(logged in)" : "(not logged in)"}
        </div>
        <div className="flex gap-3 mb-4">
          <button
            onClick={() => setTarget("personal")}
            className={`px-4 py-2 rounded-lg text-sm ${target === "personal" ? "bg-sky-600" : "bg-gray-800"} text-white`}
          >
            Personal
          </button>
          <button
            onClick={() => setTarget("professional")}
            className={`px-4 py-2 rounded-lg text-sm ${target === "professional" ? "bg-emerald-600" : "bg-gray-800"} text-white`}
          >
            Professional
          </button>
        </div>
        <button
          disabled={!target}
          onClick={startSwitch}
          className={`px-4 py-2 rounded-lg text-white text-sm ${target ? "bg-purple-600 hover:bg-purple-500" : "bg-gray-700"}`}
        >
          Switch and Re-Login
        </button>
      </div>
    </div>
  );
}

export default SwitchWorkspace;

