import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ProviderIcon from "../components/ProviderIcon.jsx";
import { fetchUserStatus } from "../services/api.js";

function ProviderConnect() {
  const { provider } = useParams();
  const navigate = useNavigate();
  const [workspace, setWorkspace] = useState("personal");
  const [status, setStatus] = useState("unknown");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    (async () => {
      const s = await fetchUserStatus();
      const ws = s.workspaceAllowed || (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) || "personal";
      setWorkspace(ws);
      fetch(`http://localhost:8080/api/integrations?workspace=${ws}`, { credentials: "include" })
        .then((res) => (res.ok ? res.json() : []))
        .then((data) => {
          if (cancelled) return;
          const connected = Array.isArray(data) && data.find((i) => i.provider === provider);
          setStatus(connected ? "connected" : "disconnected");
        })
        .catch(() => !cancelled && setStatus("disconnected"))
        .finally(() => !cancelled && setLoading(false));
    })();
    return () => { cancelled = true; };
  }, [provider]);

  const startConnect = () => {
    const base = import.meta.env.VITE_API_BASE || "http://localhost:8080";
    if (provider === "slack") {
      window.location.href = `${base}/auth/slack?workspace=${workspace}`;
    } else {
      window.location.href = `${base}/auth/oauth/${provider}/initiate?workspace=${workspace}`;
    }
  };

  return (
    <div className="min-h-screen bg-black text-white p-6">
      <div className="flex items-center gap-3 mb-4">
        <button
          onClick={() => navigate(-1)}
          className="px-3 py-1 rounded-lg bg-gray-800 text-white text-sm"
        >
          ← Back
        </button>
        <h1 className="text-xl font-semibold flex items-center gap-2">
          <ProviderIcon provider={provider} />
          <span>{provider?.toUpperCase()} Integration</span>
        </h1>
        <div className="text-sm text-gray-400 ml-auto">{workspace}</div>
      </div>
      {loading && <div>Loading…</div>}
      {!loading && (
        <div className="p-4 border border-gray-800 rounded-xl bg-black/70 max-w-xl">
          <div className="text-sm text-gray-300 mb-2">Status: {status}</div>
          <div className="flex gap-3">
            {status !== "connected" ? (
              <button
                onClick={startConnect}
                className="px-4 py-2 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-500 transition"
              >
                Connect {provider?.toUpperCase()}
              </button>
            ) : (
              <button
                onClick={() =>
                  fetch(`http://localhost:8080/api/integrations/${provider}?workspace=${workspace}`, {
                    method: "DELETE",
                    credentials: "include",
                  }).then(() => window.location.reload())
                }
                className="px-4 py-2 rounded-lg bg-gray-700 text-white text-sm hover:bg-gray-600 transition"
              >
                Disconnect
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default ProviderConnect;
