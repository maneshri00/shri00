const API_BASE = "http://localhost:8080";

export async function fetchImportantMessages(workspace) {
  const url =
    workspace === "professional"
      ? `${API_BASE}/dashboards/professional/important-messages?bucket=today&unread_only=true`
      : `${API_BASE}/dashboards/personal/important-messages?bucket=today&unread_only=true`;
  const res = await fetch(url, { credentials: "include" });
  if (!res.ok) throw new Error("Failed to load important messages");
  return res.json();
}

export async function fetchCommands(workspace) {
  const url =
    workspace === "professional"
      ? `${API_BASE}/dashboards/professional/commands?limit=50`
      : `${API_BASE}/dashboards/personal/commands?limit=50`;
  const res = await fetch(url, { credentials: "include" });
  if (!res.ok) throw new Error("Failed to load commands");
  return res.json();
}

export async function executeCommand(command_id) {
  const res = await fetch(`${API_BASE}/commands/execute`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ command_id }),
  });
  if (!res.ok) throw new Error("Failed to execute command");
  return res.json().catch(() => ({}));
}

export async function fetchUserStatus() {
  try {
    const res = await fetch(`${API_BASE}/api/user/status`, {
      credentials: "include",
    });
    if (!res.ok) return { loggedIn: false };
    const data = await res.json().catch(() => ({}));
    const loggedIn = !!data.loggedIn;
    return { loggedIn, ...data };
  } catch {
    return { loggedIn: false };
  }
}

export async function fetchTasks(workspace, bucket) {
  const res = await fetch(
    `${API_BASE}/tasks?workspace_id=${workspace}&bucket=${bucket}`,
    { credentials: "include" }
  );
  if (!res.ok) throw new Error("Failed to load tasks");
  return res.json();
}

export async function fetchNotifications(workspace) {
  const res = await fetch(
    `${API_BASE}/notifications?workspace_id=${workspace}`,
    { credentials: "include" }
  );
  if (!res.ok) throw new Error("Failed to load notifications");
  return res.json();
}

export async function markNotificationRead(id) {
  const res = await fetch(`${API_BASE}/notifications/${id}/read`, {
    method: "PATCH",
    credentials: "include",
  });
  if (!res.ok) throw new Error("Failed to mark notification read");
  return res.json().catch(() => ({}));
}
