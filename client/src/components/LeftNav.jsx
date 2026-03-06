import React from "react";
import { useNavigate } from "react-router-dom";

function LeftNav({ workspace = "personal" }) {
  const navigate = useNavigate();
  const items = [
    { label: "Dashboard", to: workspace === "professional" ? "/professional" : "/personal" },
    { label: "Tasks", to: "/TaskPage" },
    { label: "Add Task", to: "/AddTask" },
    { label: "Completed", to: "/CompletedTask" },
    { label: "Graph", to: "/Graph" },
    { label: "Integrations", to: "/integrations" },
    { label: "Connections", to: "/connections" },
    { label: "Profile", to: "/profile" },
    { label: "Switch Workspace", to: "/switch-workspace" },
  ];

  return (
    <div className="hidden lg:block w-56 border-r border-gray-800 bg-black/60">
      <div className="p-4 text-xs text-gray-400">{workspace}</div>
      <nav className="flex flex-col">
        {items.map((it) => (
          <button
            key={it.label}
            onClick={() => navigate(it.to)}
            className="text-left px-4 py-2 text-sm text-gray-200 hover:bg-gray-800 hover:text-white"
          >
            {it.label}
          </button>
        ))}
      </nav>
    </div>
  );
}

export default LeftNav;

