import React, { useEffect, useState } from "react";
import IntegrationButtons from "../components/IntegrationButtons.jsx";
import NeonProgress from "../homePage/NeonProgress.jsx";
import LineChart from "../homePage/ProgressLineChart.jsx";
import { useNavigate } from "react-router-dom";
import { fetchUserStatus } from "../services/api.js";

function ConnectionsHome() {
  const navigate = useNavigate();
  const [wsState, setWsState] = useState((typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) || "personal");

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const s = await fetchUserStatus();
      if (!cancelled) {
        const w = s.workspaceAllowed || wsState;
        setWsState(w || "personal");
      }
    })();
    return () => { cancelled = true; };
  }, []);

  return (
    <div className="min-h-screen bg-[#0F0F0F] text-rose-50 flex flex-col">
      <div className="h-[10%] flex items-center px-6">
        <div className="flex-1 text-white font-semibold">Tusk</div>
        <div className="flex gap-6">
          <button className="hover:text-pink-500" onClick={() => navigate(wsState === "professional" ? "/professional" : "/personal")}>
            Back to Dashboard
          </button>
        </div>
        <div className="flex-1 flex justify-end">
          <span className="text-xs text-gray-400">{wsState}</span>
        </div>
      </div>

      <div className="h-[80%] flex bg-black">
          <div className="w-1/2 flex flex-col gap-6 items-center justify-center px-6">
          <div className="text-2xl font-semibold">Connect your tools</div>
          <div className="w-full max-w-xl">
            <IntegrationButtons workspaceType={wsState} />
          </div>
          <div className="grid grid-cols-2 gap-4 w-full max-w-xl">
            <ProviderCard name="GitHub" color="bg-gray-800" />
            <ProviderCard name="Discord" color="bg-indigo-600" />
            <ProviderCard name="Slack" color="bg-purple-600" />
            <ProviderCard name="Jira" color="bg-blue-600" />
            <ProviderCard name="Notion" color="bg-black border border-white/20" />
          </div>
        </div>

        <div className="w-1/2 relative">
          <div className="font-bold text-3xl p-7 w-150">
            Unified connections for smarter task automation
          </div>
          <div className="absolute top-7 right-7">
            <NeonProgress percent={75} />
          </div>
          <div className="absolute bottom-1 right-5 w-full">
            <LineChart />
            <div className="flex justify-center">Integration Activity</div>
          </div>
        </div>
      </div>

      <div className="h-[10%] flex items-center justify-center">
        <div className="flex gap-8">
          <button className="hover:text-pink-500" onClick={() => navigate("/integrations")}>
            Manage Integrations
          </button>
          <button className="hover:text-pink-500" onClick={() => navigate(wsState === "professional" ? "/professional" : "/personal")}>
            Back to {wsState} Dashboard
          </button>
        </div>
      </div>
    </div>
  );
}

function ProviderCard({ name, color }) {
  return (
    <div className={`p-4 rounded-xl ${color}`}>
      <div className="text-white font-semibold">{name}</div>
      <div className="text-xs text-gray-200">Click Connect above to authorize</div>
    </div>
  );
}

export default ConnectionsHome;
