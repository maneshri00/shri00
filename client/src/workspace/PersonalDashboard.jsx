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

function PersonalDashboard() {
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
          fetchImportantMessages("personal"),
          fetchCommands("personal"),
          fetchTasks("personal", "today").catch(() => []),
          fetchTasks("personal", "week").catch(() => []),
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
          setMessages(getSampleMessages("personal"));
          setCommands(getSampleCommands("personal"));
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
      <LeftNav workspace="personal" />
      <div className="flex-1">
      <div className="flex items-center justify-between px-6 py-4 border-b border-gray-800">
        <h1 className="text-xl font-semibold">Personal Dashboard</h1>
        <div className="text-sm text-gray-400">Today</div>
      </div>

      {loading && <div className="p-4">Loading...</div>}
      {error && <div className="p-4 text-red-400">{error}</div>}

      {!loading && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 p-6">
          <div className="lg:col-span-3">
            <SummaryHeader workspaceType="personal" />
          </div>
          <div className="lg:col-span-3">
            <WorkspaceBadge initial="personal" />
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
            <ConnectedAccounts workspaceType="personal" />
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
            <IntegrationButtons workspaceType="personal" />
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
            <TaskBuckets workspaceType="personal" />
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
      source_platform: workspace === "personal" ? "Gmail Personal" : "Gmail Work",
      sender: "Alice",
      subject: "Dinner plan",
      preview: "Shall we book for Friday evening?",
      received_at: new Date().toISOString(),
      unread: true,
      priority: "HIGH",
    },
    {
      id: "m2",
      source_platform: workspace === "personal" ? "Google Calendar" : "Outlook Calendar",
      sender: "Calendar",
      subject: "Event: Dentist",
      preview: "Appointment tomorrow 10:30",
      received_at: new Date().toISOString(),
      unread: true,
      priority: "MEDIUM",
    },
  ];
}

function getSampleCommands(workspace) {
  return [
    {
      command_id: "c1",
      label: "Schedule calendar event",
      action_type: "calendar.schedule",
      target_platform: workspace === "personal" ? "Google Calendar" : "Outlook Calendar",
      payload_summary: "Dentist, tomorrow 10:30",
      linked_task_id: null,
      requires_confirmation: false,
    },
    {
      command_id: "c2",
      label: "Reply to email",
      action_type: "gmail.reply",
      target_platform: workspace === "personal" ? "Gmail Personal" : "Gmail Work",
      payload_summary: "Re: Dinner plan",
      linked_task_id: null,
      requires_confirmation: true,
    },
  ];
}

export default PersonalDashboard;

