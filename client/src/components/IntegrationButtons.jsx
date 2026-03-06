import React from "react";

function IntegrationButtons({ workspaceType }) {
  const goto = (provider) => {
    const base = import.meta.env.VITE_API_BASE || "http://localhost:8080";
    const ws = workspaceType || (typeof localStorage !== "undefined" ? localStorage.getItem("workspacePreferred") : "");
    const url = `${base}/auth/oauth/${provider}/initiate${ws ? `?workspace=${ws}` : ""}`;
    window.location.href = url;
  };

  const Button = ({ label, color, onClick }) => (
    <button
      onClick={onClick}
      className={`px-4 py-2 rounded-lg text-white text-sm ${color} hover:opacity-90 transition`}
    >
      {label}
    </button>
  );

  return (
    <div className="p-4 rounded-xl border border-gray-800 bg-black/70">
      <div className="text-sm text-gray-300 mb-3">Connect Integrations</div>
      <div className="flex flex-wrap gap-3">
        <Button label="Connect GitHub" color="bg-gray-800" onClick={() => goto("github")} />
        <Button label="Connect Discord" color="bg-indigo-600" onClick={() => goto("discord")} />
        <Button label="Connect Slack" color="bg-purple-600" onClick={() => goto("slack")} />
        <Button label="Connect Jira" color="bg-blue-600" onClick={() => goto("jira")} />
        <Button label="Connect Notion" color="bg-black border border-white/20" onClick={() => goto("notion")} />
      </div>
    </div>
  );
}

export default IntegrationButtons;

