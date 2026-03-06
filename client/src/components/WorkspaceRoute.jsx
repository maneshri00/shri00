import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { fetchUserStatus } from "../services/api.js";
import AccessDenied from "./AccessDenied.jsx";

function WorkspaceRoute({ workspace, children }) {
  const [status, setStatus] = useState({ loading: true, loggedIn: false, workspaceAllowed: null });

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const s = await fetchUserStatus();
      if (!cancelled) setStatus({ loading: false, ...s });
    })();
    return () => { cancelled = true; };
  }, []);

  if (status.loading) return <div className="p-4 text-white">Loading…</div>;

  if (!status.loggedIn) return children;

  if (status.workspaceAllowed && status.workspaceAllowed !== workspace) {
    return <AccessDenied allowed={status.workspaceAllowed} />;
  }

  return children;
}

export default WorkspaceRoute;
