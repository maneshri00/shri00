import React from "react";
import { useNavigate } from "react-router-dom";

function AccessDenied({ allowed }) {
  const navigate = useNavigate();
  return (
    <div className="min-h-screen bg-black text-white flex flex-col items-center justify-center">
      <div className="text-2xl font-semibold">Access Denied</div>
      <div className="text-sm text-gray-400 mt-2">You are allowed to access {allowed} workspace only</div>
      <div className="mt-6 flex gap-3">
        <button
          onClick={() => navigate(`/${allowed || ""}`)}
          className="px-4 py-2 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-500"
        >
          Go to {allowed} dashboard
        </button>
        <button
          onClick={() => navigate("/")}
          className="px-4 py-2 rounded-lg bg-gray-700 text-white text-sm hover:bg-gray-600"
        >
          Home
        </button>
      </div>
    </div>
  );
}

export default AccessDenied;

