import { useEffect, useState, useRef } from "react";

import axios from "axios";
import React from "react";
import { useNavigate } from "react-router-dom";
import NeonProgress from "./NeonProgress";
import LineChart from "./ProgressLineChart";
import CompletedTask from "../CompletedTaskPage/CompletedTask";
import { fetchUserStatus } from "../services/api.js";

const HomePage = ({ tasks, setTasks }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [workspace, setWorkspace] = useState("personal");

  const navigate = useNavigate();
  const completedCount = tasks.filter((t) => t.status === "DONE").length;
  const remainingCount = tasks.filter((t) => t.status !== "DONE").length;
  const totalCount = tasks.length;

  const progressPercent =
    totalCount === 0 ? 0 : Math.round((completedCount / totalCount) * 100);

 const notificationIntervalRef = useRef(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const status = await fetchUserStatus();
        const ws =
          status.workspaceAllowed ||
          (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) ||
          "personal";
        if (!cancelled) setWorkspace(ws);
        const url = `http://localhost:8080/tasks/priority?workspace=${ws}`;
        const res = await axios.get(url, { withCredentials: true });
        if (!cancelled) {
          setTasks(res.data);
          setLoading(false);
        }
      } catch (e) {
        if (!cancelled) {
          setError("Failed to load tasks");
          setLoading(false);
        }
      }
    })();
    return () => { cancelled = true; };
  }, []);
  useEffect(() => {
  if (Notification.permission !== "granted") return;

  setTimeout(() => {
    new Notification("🔥 TEST NOTIFICATION", {
      body: "If you see this, browser notifications work",
    });
  }, 2000);
}, []);


  useEffect(() => {
  if ("Notification" in window && Notification.permission === "default") {
    Notification.requestPermission();
  }
}, []);

const showPriorityNotification = () => {
  if (!("Notification" in window)) return;
  if (Notification.permission !== "granted") return;

  const firstHighPriorityTask = tasks.find(
    (t) =>
      t.status !== "DONE" &&
      String(t.priority).toUpperCase() === "HIGH"
  );

  if (!firstHighPriorityTask) return;

  new Notification("🔥 First Priority Task", {
    body: firstHighPriorityTask.title,
    icon: "/logo192.png",
    tag: `priority-${firstHighPriorityTask.id}`,
    renotify: true,
  });
};

useEffect(() => {
  if (loading) return;

  // 🔔 first time
  showPriorityNotification();

  // 🔁 every 60 minutes
  notificationIntervalRef.current = setInterval(() => {
    showPriorityNotification();
  }, 60 * 60 * 1000);

  return () => {
    clearInterval(notificationIntervalRef.current);
  };
}, [loading]); // ❗ REMOVE tasks dependency



  const handleComplete = (id) => {
    if (!id) return;


    setTasks((prev) =>
      prev.map((task) => (task.id === id ? { ...task, status: "DONE" } : task)),
    );

 
    axios
      .put(`http://localhost:8080/tasks/${id}/complete?workspace=${workspace}`, null, { withCredentials: true })
      .then(() => axios.get(`http://localhost:8080/tasks/priority?workspace=${workspace}`, { withCredentials: true }))
      .then((res) => {
        setTasks(res.data);
      })
      .catch(() => {
        console.log("Failed to mark task done");
      });
  };
  const firstTask = tasks.find(
  (t) => t.status !== "DONE" && t.priority === "HIGH"
);




  return (
    <div className="h-screen bg-[#0F0F0F] flex flex-col text-rose-50">
      {loading && <div className="p-4">Loading tasks...</div>}
      {error && <div className="p-4">{error}</div>}

      {!loading && !error && (
        <>
          {/* TOP BAR */}
          <div className="h-[10%]  flex items-center px-6">
            <div className="flex-1 text-white font-semibold">Tusk</div>

            <div className="flex gap-6 ">
              <button
                className="hover:text-pink-500 cursor-pointer"
                onClick={() => navigate("/ChangePriority")}
              >
                Change Priority
              </button>

              <button className="hover:text-pink-500 cursor-pointer">
                Connect to Notion
              </button>

              <button
                className="hover:text-pink-500 cursor-pointer"
                onClick={() => navigate("/ChangeGoal")}
              >
                Change Goal
              </button>
            </div>

            <div className="flex-1 flex justify-end">
              <button onClick={() => navigate("/profile")}>Profile</button>
            </div>
          </div>

          {/* MAIN */}
          <div className="h-[80%] flex bg-black">
            {/* LEFT */}
            <div className="w-1/2 flex justify-center">
              <div
                className="h-full w-full max-w-md overflow-y-auto p-4 hide-scrollbar"
                style={{ scrollbarWidth: "none" }}
              >
                <h2 className="text-xl mb-4">Priority Tasks</h2>

                {tasks.filter((t) => t.status !== "DONE").length === 0 && (
                  <p className="text-gray-400">No tasks found</p>
                )}

                {tasks
                  .filter((task) => task.status !== "DONE")
                  .map((task) => (
                    <div
                      key={task.id || task.externalId}
                      className="relative mb-6"
                    >
                      {/* CARD */}
                      <div
                        className="
                          relative
                          rounded-xl
                          bg-black/70
                          backdrop-blur-xl
                          border border-sky-400/50
                          p-4
                          shadow-[
                            0_32px_85px_rgba(0,0,0,0.95),
                            0_0_110px_rgba(56,189,248,0.85)
                          ]
                        "
                      >
                        <h4 className="font-semibold text-white">
                          {task.title}
                        </h4>

                        <p className="text-gray-300 text-sm">
                          {task.description}
                        </p>

                        <div className="flex gap-4 text-xs text-gray-400 mt-2">
                          <span>Priority: {task.priority}</span>
                          <span>Source: {task.source}</span>
                        </div>

                        {/* DONE CHECKBOX */}
                        <label
                          className="
                            absolute bottom-3 right-3
                            flex items-center gap-2
                            cursor-pointer group
                          "
                        >
                          {task.status !== "DONE" && (
                            <input
                              type="checkbox"
                              onChange={() => handleComplete(task.id)}
                              className="peer hidden"
                            />
                          )}

                          <div
                            className="
                              w-4 h-4
                              border border-sky-400
                              rounded-sm
                              flex items-center justify-center
                              transition
                            "
                          >
                            <svg
                              className="
                                w-3 h-3
                                text-sky-400
                                opacity-0
                                group-hover:opacity-100
                                peer-checked:opacity-100
                                transition
                              "
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="3"
                              viewBox="0 0 24 24"
                            >
                              <path d="M5 13l4 4L19 7" />
                            </svg>
                          </div>

                          <span className="text-xs text-sky-300">Done</span>
                        </label>
                      </div>
                    </div>
                  ))}
              </div>
            </div>

            {/* RIGHT */}
            <div className="w-1/2 relative">
              <div className="font-bold text-3xl p-7 -ml-30 w-150">
                Everything you need to plan smarter and work better—right at
                your fingertips
              </div>
              <div className="absolute top-7 right-7">
                <NeonProgress percent={progressPercent} />
              </div>

              <div className="absolute bottom-1 right-5 w-full">
                <LineChart />
                <div className="flex justify-center">Weekly Score</div>
              </div>
            </div>
          </div>

          {/* FOOTER */}
          <div className="h-[10%]  flex items-center justify-center">
            <div className="flex gap-8">
              <button
                className="hover:text-pink-500 cursor-pointer"
                onClick={() => navigate("/AddTask")}
              >
                Add Task
              </button>

              <button
                className="hover:text-pink-500 cursor-pointer"
                onClick={() => navigate("/TaskPage")}
              >
                Task List
              </button>

              <button
                className="hover:text-pink-500 cursor-pointer"
                onClick={() => navigate("/CompletedTask")}
              >
                Completed Tasks
              </button>

              <button
                className="hover:text-pink-500 cursor-pointer"
                onClick={() => navigate("/Graph")}
              >
                Graph
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default HomePage;
