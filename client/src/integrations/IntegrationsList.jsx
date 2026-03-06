import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ProviderIcon from "../components/ProviderIcon.jsx";
import { fetchUserStatus } from "../services/api.js";

function IntegrationsList() {
  const [items, setItems] = useState([]);
  const [workspace, setWorkspace] = useState("personal");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    (async () => {
      const s = await fetchUserStatus();
      const ws = s.workspaceAllowed || (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) || "personal";
      setWorkspace(ws);
      fetch(`http://localhost:8080/api/integrations?workspace=${ws}`, { credentials: "include" })
        .then((res) => (res.ok ? res.json() : []))
        .then((data) => !cancelled && setItems(Array.isArray(data) ? data : []))
        .catch(() => !cancelled && setItems([]))
        .finally(() => !cancelled && setLoading(false));
    })();
    return () => { cancelled = true; };
  }, []);

  return (
    <div className="min-h-screen bg-black text-white p-6">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold">Integrations</h1>
        <div className="text-sm text-gray-400">{workspace}</div>
      </div>
      {loading && <div>Loading…</div>}
      {!loading && (
        <>
          <div className="flex items-center justify-between mb-3">
            <div className="text-sm text-gray-300">Connected {items.length} / 5</div>
            <button
              onClick={() => {
                fetch(`http://localhost:8080/api/integrations?workspace=${workspace}`, {
                  method: "DELETE",
                  credentials: "include",
                }).then(() => window.location.reload());
              }}
              className="px-3 py-1 rounded-lg bg-gray-800 text-white text-xs hover:bg-gray-700"
            >
              Disconnect All
            </button>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {["github", "discord", "slack", "jira", "notion"].map((p) => {
              const connected = items.find((i) => i.provider === p);
              return (
                <div key={p} className="p-4 border border-gray-800 rounded-xl bg-black/70">
                  <div className="text-white font-semibold mb-1 flex items-center gap-2">
                    <ProviderIcon provider={p} />
                    <span>{p.toUpperCase()}</span>
                  </div>
                  <div className="text-xs text-gray-400 mb-3">
                    {connected ? `Connected ${connected.connected_at || ""}` : "Not connected"}
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => navigate(`/integrations/${p}`)}
                      className="px-3 py-1 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-500 transition"
                    >
                      Manage
                    </button>
                    {connected && (
                      <button
                        onClick={() => {
                          fetch(`http://localhost:8080/api/integrations/${p}?workspace=${workspace}`, {
                            method: "DELETE",
                            credentials: "include",
                          }).then(() => window.location.reload());
                        }}
                        className="px-3 py-1 rounded-lg bg-gray-700 text-white text-sm hover:bg-gray-600 transition"
                      >
                        Disconnect
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </>
      )}
    </div>
  );
}

export default IntegrationsList;
