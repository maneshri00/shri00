import React from "react";

const COLORS = {
  github: "bg-gray-800",
  discord: "bg-indigo-600",
  slack: "bg-purple-600",
  jira: "bg-blue-600",
  notion: "bg-black border border-white/20",
};

function ProviderIcon({ provider, size = "w-6 h-6" }) {
  const label = (provider || "").toUpperCase().slice(0, 2);
  const color = COLORS[provider] || "bg-gray-700";
  return (
    <div className={`inline-flex items-center justify-center rounded-full ${color} ${size} text-[10px] text-white`}>
      {label}
    </div>
  );
}

export default ProviderIcon;

