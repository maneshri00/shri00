import React from "react";
import { useSearchParams, useNavigate } from "react-router-dom";

function ContinueWithGoogle() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const wsParam = params.get("workspace");
  const wsLocal = (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) || "";
  const workspace = wsParam || wsLocal || "personal";

  const startLogin = () => {
    const base = import.meta.env.VITE_API_BASE || "http://localhost:8080";
    window.location.href = `${base}/auth/google?workspace=${workspace}`;
  };

  return (
    <div className="min-h-screen bg-black text-white flex flex-col items-center justify-center">
      <div className="text-2xl font-semibold mb-2">Continue with Google</div>
      <div className="text-sm text-gray-400 mb-6">Selected workspace: {workspace}</div>
      <div className="flex gap-3">
        <button onClick={() => navigate(-1)} className="px-4 py-2 rounded-lg bg-gray-800 text-white text-sm">Back</button>
        <button onClick={startLogin} className="px-4 py-2 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-500">Continue with Google</button>
      </div>
    </div>
  );
}

export default ContinueWithGoogle;

