import React, { useEffect, useState } from "react";
import CommandList from "../components/CommandList.jsx";
import ImportantMessageList from "../components/ImportantMessageList.jsx";
import SummaryHeader from "../components/SummaryHeader.jsx";
import TaskBuckets from "../components/TaskBuckets.jsx";
import IntegrationButtons from "../components/IntegrationButtons.jsx";
import ConnectedAccounts from "../components/ConnectedAccounts.jsx";
import WorkspaceBadge from "../components/WorkspaceBadge.jsx";
import { fetchImportantMessages, fetchCommands, fetchTasks } from "../services/api.js";
import NeonProgress from "../homePage/NeonProgress.jsx";
import LineChart from "../homePage/ProgressLineChart.jsx";
import LeftNav from "../components/LeftNav.jsx";

function ProfessionalDashboard() {
  const [messages, setMessages] = useState([]);
  const [commands, setCommands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [progressPercent, setProgressPercent] = useState(0);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    const fetchData = async () => {
      try {
        const [msgs, cmds, tToday, tWeek] = await Promise.all([
          fetchImportantMessages("professional"),
          fetchCommands("professional"),
          fetchTasks("professional", "today").catch(() => []),
          fetchTasks("professional", "week").catch(() => []),
        ]);
        if (!cancelled) {
          setMessages(Array.isArray(msgs) ? msgs : []);
          setCommands(Array.isArray(cmds) ? cmds : []);
          const allTasks = [...(Array.isArray(tToday) ? tToday : []), ...(Array.isArray(tWeek) ? tWeek : [])];
          const total = allTasks.length;
          const completed = allTasks.filter((t) => String(t.status).toUpperCase() === "DONE").length;
          setProgressPercent(total === 0 ? 0 : Math.round((completed / total) * 100));
        }
      } catch (e) {
        if (!cancelled) {
          setMessages(getSampleMessages("professional"));
          setCommands(getSampleCommands("professional"));
          setProgressPercent(0);
          setError(null);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchData();
    return () => { cancelled = true; };
  }, []);

  return (
    <div className="min-h-screen bg-black text-white flex">
      <LeftNav workspace="professional" />
      <div className="flex-1">
      <div className="flex items-center justify-between px-6 py-4 border-b border-gray-800">
        <h1 className="text-xl font-semibold">Professional Dashboard</h1>
        <div className="text-sm text-gray-400">Today</div>
      </div>

      {loading && <div className="p-4">Loading...</div>}
      {error && <div className="p-4 text-red-400">{error}</div>}

      {!loading && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 p-6">
          <div className="lg:col-span-3">
            <SummaryHeader workspaceType="professional" />
          </div>
          <div className="lg:col-span-3">
            <WorkspaceBadge initial="professional" />
          </div>
          <div className="lg:col-span-3">
            <div className="p-4 rounded-xl border border-gray-800 bg-black/70 flex justify-between items-center">
              <div className="text-sm text-gray-300">Connections</div>
              <div className="flex gap-3">
                <button onClick={() => window.location.href='/connections'} className="px-3 py-1 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-500">Open Connections</button>
                <button onClick={() => window.location.href='/integrations'} className="px-3 py-1 rounded-lg bg-gray-700 text-white text-sm hover:bg-gray-600">Manage</button>
                <button onClick={() => window.location.href='/switch-workspace'} className="px-3 py-1 rounded-lg bg-purple-600 text-white text-sm hover:bg-purple-500">Switch Workspace</button>
              </div>
            </div>
          </div>
          <div className="lg:col-span-3">
            <ConnectedAccounts workspaceType="professional" />
          </div>
          <div className="col-span-1">
            <h2 className="text-lg mb-3">Command Center</h2>
            <CommandList commands={commands} />
          </div>
          <div className="col-span-1">
            <h2 className="text-lg mb-3">Important Messages</h2>
            <ImportantMessageList messages={messages} />
          </div>
          <div className="col-span-1">
            <IntegrationButtons workspaceType="professional" />
          </div>
          <div className="col-span-1 relative">
            <div className="absolute top-0 right-0">
              <NeonProgress percent={progressPercent} />
            </div>
            <div className="mt-28">
              <LineChart />
              <div className="flex justify-center text-xs text-gray-400">Weekly Score</div>
            </div>
          </div>
          <div className="lg:col-span-3">
            <TaskBuckets workspaceType="professional" />
          </div>
        </div>
      )}
      </div>
    </div>
  );
}

function getSampleMessages(workspace) {
  return [
    {
      id: "m1",
      source_platform: "Slack",
      sender: "Team Lead",
      subject: "Standup reminder",
      preview: "Standup in 30 minutes; share blockers.",
      received_at: new Date().toISOString(),
      unread: true,
      priority: "HIGH",
    },
    {
      id: "m2",
      source_platform: "Jira",
      sender: "Jira",
      subject: "Issue assigned: PROD-123",
      preview: "Investigate error rate on login flow.",
      received_at: new Date().toISOString(),
      unread: true,
      priority: "HIGH",
    },
  ];
}

function getSampleCommands(workspace) {
  return [
    {
      command_id: "c10",
      label: "Create Jira Issue",
      action_type: "jira.create",
      target_platform: "Jira",
      payload_summary: "Bug: Login error rate spike",
      linked_task_id: null,
      requires_confirmation: false,
    },
    {
      command_id: "c11",
      label: "Reply on Slack",
      action_type: "slack.reply",
      target_platform: "Slack",
      payload_summary: "Acknowledged; ETA by EOD",
      linked_task_id: null,
      requires_confirmation: true,
    },
  ];
}

export default ProfessionalDashboard;
