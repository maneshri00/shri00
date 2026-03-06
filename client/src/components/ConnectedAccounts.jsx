import React, { useEffect, useState } from "react";
import ProviderIcon from "./ProviderIcon.jsx";

function ConnectedAccounts({ workspaceType }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    const ws = workspaceType || (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) || "personal";
    fetch(`http://localhost:8080/api/integrations?workspace=${ws}`, { credentials: "include" })
      .then((res) => (res.ok ? res.json() : []))
      .then((data) => !cancelled && setItems(Array.isArray(data) ? data : []))
      .catch(() => !cancelled && setItems([]))
      .finally(() => !cancelled && setLoading(false));
    return () => { cancelled = true; };
  }, [workspaceType]);

  const providers = ["github", "discord", "slack", "jira", "notion"];
  const isConnected = (p) => items.some((i) => i.provider === p && i.status === "connected");

  return (
    <div className="p-4 rounded-xl border border-gray-800 bg-black/70">
      <div className="flex items-center justify-between">
        <div className="text-sm text-gray-300">Connected Accounts</div>
        <a href="/integrations" className="text-xs text-sky-400 hover:text-sky-300">Manage</a>
      </div>
      <div className="mt-3 flex flex-wrap gap-3">
        {providers.map((p) => (
          <div key={p} className={`px-3 py-2 rounded-lg text-xs border flex items-center gap-2 ${isConnected(p) ? 'border-emerald-500 text-emerald-300' : 'border-gray-700 text-gray-400'}`}>
            <ProviderIcon provider={p} />
            <span className="uppercase">{p}</span>
            <span className="ml-2">{isConnected(p) ? "Connected" : "Not connected"}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ConnectedAccounts;
