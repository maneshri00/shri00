import React, { useEffect, useState } from "react";
import { Routes, Route, useNavigate, useLocation } from "react-router-dom";
import LandingPage from "./landingPage/LandingPage.jsx";
import "./App.css"
import DashBoard from "./dashBoard/DashBoard.jsx";
import CreateAccountForm from "./dashBoard/CreateAccountForm.jsx";
import Graph from "./GraphPage/Graph.jsx";
import CompletedTask from "./CompletedTaskPage/CompletedTask.jsx";
import TaskPage from "./TaskListPage/TaskPage.jsx";
import AddTask from "./AddTaskPage/AddTask.jsx";
import PriorityPage from "./ChangePriorityPage/PriorityPage.jsx";
import GoalPage from "./ChangeGoalPage/GoalPage.jsx";
import ViewProfilePage from "./ProfilePage/ViewProfilePage.jsx";
import PersonalDashboard from "./workspace/PersonalDashboard.jsx";
import ProfessionalDashboard from "./workspace/ProfessionalDashboard.jsx";
import { fetchUserStatus } from "./services/api.js";
import IntegrationsList from "./integrations/IntegrationsList.jsx";
import ProviderConnect from "./integrations/ProviderConnect.jsx";
import ConnectionsHome from "./integrations/ConnectionsHome.jsx";
import WorkspaceRoute from "./components/WorkspaceRoute.jsx";
import SwitchWorkspace from "./settings/SwitchWorkspace.jsx";
import ContinueWithGoogle from "./auth/ContinueWithGoogle.jsx";
import ErrorBoundary from "./components/ErrorBoundary.jsx";
const App = () => {
  const [tasks, setTasks] = useState([]);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const status = await fetchUserStatus();
      if (cancelled) return;
      if (status.loggedIn && location.pathname === "/") {
        let pref = "";
        try { pref = localStorage.getItem("workspacePreferred") || ""; } catch {}
        if (pref === "personal" || pref === "professional") {
          navigate(`/${pref}`);
        }
      }
    })();
    return () => { cancelled = true; };
  }, [location.pathname, navigate]);
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/personal" element={<WorkspaceRoute workspace="personal"><PersonalDashboard /></WorkspaceRoute>} />
      <Route path="/professional" element={<WorkspaceRoute workspace="professional"><ProfessionalDashboard /></WorkspaceRoute>} />
      <Route path="/integrations" element={<IntegrationsList />} />
      <Route path="/integrations/:provider" element={<ProviderConnect />} />
      <Route path="/connections" element={<ConnectionsHome />} />
      <Route path="/switch-workspace" element={<SwitchWorkspace />} />
      <Route path="/continue" element={<ContinueWithGoogle />} />
      <Route path="/dashboard" element={<DashBoard />} />
      <Route path="/form" element={<CreateAccountForm />} />
      <Route path="/Graph" element={<Graph />} />
      <Route path="/TaskPage" element={<TaskPage />} />
      <Route path="/AddTask" element={<AddTask />} />
      <Route path="/ChangePriority" element={<PriorityPage />} />
      <Route path="/ChangeGoal" element={<GoalPage />} />
      <Route path="/profile" element={<ErrorBoundary><ViewProfilePage /></ErrorBoundary>} />

        
        <Route
          path="/CompletedTask"
          element={<CompletedTask tasks={tasks} />}
        />
    </Routes>
  );
};

export default App;
