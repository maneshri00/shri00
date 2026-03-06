import React, { useEffect, useState } from "react";
import { fetchUserStatus } from "../services/api.js";

function ViewProfilePage() {

  const [profile, setProfile] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const base = import.meta.env.VITE_API_BASE || "http://localhost:8080";
    let cancelled = false;
    (async () => {
      try {
        const status = await fetchUserStatus();
        const ws =
          status.workspaceAllowed ||
          (typeof localStorage !== "undefined" && localStorage.getItem("workspacePreferred")) ||
          "";
        const url = `${base}/api/profile/profile-view${ws ? `?workspace=${ws}` : ""}`;
        const res = await fetch(url, { credentials: "include" });
        if (!res.ok) {
          const text = await res.text();
          throw new Error(text || `HTTP ${res.status}`);
        }
        const data = await res.json();
        if (!cancelled) setProfile(data);
      } catch (e) {
        if (!cancelled) setError("Failed to load profile");
      }
    })();
    return () => { cancelled = true; };
  }, []);

  if (error) return <p className="text-white">{error}</p>;
  if (!profile) return <p className="text-white">Loading...</p>;

  return (
    <div className="min-h-screen bg-black text-white p-8">

      <h1 className="text-2xl text-sky-400 mb-6">My Profile</h1>

      <div className="space-y-4">

        <p><b>Name:</b> {profile.name}</p>
        <p><b>Age:</b> {profile.age}</p>
        <p><b>DOB:</b> {profile.dob}</p>
        <p><b>Wake Time:</b> {profile.wakeTime}</p>
        <p><b>Sleep Time:</b> {profile.sleepTime}</p>
        <p><b>Completed Tasks:</b> {profile.completedTaskCount}</p>

      </div>

    </div>
  );
}

export default ViewProfilePage;
