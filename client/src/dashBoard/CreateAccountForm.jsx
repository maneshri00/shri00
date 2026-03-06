import React from "react";
import BackGroundimage from "./BackGroundimage.jpg";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const CreateAccountForm = () => {
  const [name, setName] = useState("");
  const [profession, setProfession] = useState("");
  const [goal, setGoal] = useState("");
  const [wakeTime, setWakeTime] = useState("");
  const [sleepTime, setSleepTime] = useState("");
  const [priorityInput, setPriorityInput] = useState("");
  const [priorities, setPriorities] = useState([]);
  const navigate = useNavigate();

  return (
    <div className="relative min-h-screen overflow-hidden">
      <div
        className="
          fixed top-1/2 left-1/2
          w-[100vh] h-[100vw]
          -translate-x-1/2 -translate-y-1/2
          rotate-90
          bg-cover
          blur-[20px] scale-110
          -z-10
        "
        style={{ backgroundImage: `url(${BackGroundimage})` }}
      />

      <form
        className="
          absolute top-10 left-1/2
          -translate-x-1/2
          w-100
          p-6
          flex flex-col gap-3
          rounded-xl
          text-1xl
          z-10 text-amber-50 border border-red-500
        "
        style={{
          backgroundImage: `url(${BackGroundimage})`,
          backgroundRepeat: "no-repeat",
          backgroundSize: "200% 200%",
          backgroundPosition: "50% 35%",
          filter: "none",
        }}
      >
        {/* Name */}
        <label className="block text-sm text-white">Name</label>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full  bg-transparent border-b-2 border-gray-400 outline-none focus:border-blue-500"
        />

        {/* Profession */}
        <label className="block text-sm text-white mt-3">Profession</label>
        <input
          type="text"
          value={profession}
          onChange={(e) => setProfession(e.target.value)}
          className="w-full  bg-transparent border-b-2 border-gray-400 outline-none focus:border-blue-500"
        />

        {/* Goal */}
        <label className="block text-sm text-white mt-3">Goal</label>
        <input
          type="text"
          value={goal}
          onChange={(e) => setGoal(e.target.value)}
          className="w-full  bg-transparent border-b-2 border-gray-400 outline-none focus:border-blue-500"
        />

        <div className="flex gap-6 w-full">
          {/* Wake Time Box */}
          <div className="flex-1 p-4 border border-gray-300 rounded-xl">
            <label className="block text-sm font-medium text-white mb-2">
              Wake Up Time
            </label>

            <input
              type="time"
              value={wakeTime}
              onChange={(e) => setWakeTime(e.target.value)}
              className="w-full  rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Sleep Time Box */}
          <div className="flex-1 p-4 border border-gray-300 rounded-xl">
            <label className="block text-sm font-medium text-white mb-2">
              Sleep Time
            </label>

            <input
              type="time"
              value={sleepTime}
              onChange={(e) => setSleepTime(e.target.value)}
              className="w-full  rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        <label className="block mb-2 text-sm text-white">Priorities</label>

        <input
          type="text"
          value={priorityInput}
          placeholder="Type a priority and press Enter"
          onChange={(e) => setPriorityInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && priorityInput.trim()) {
              e.preventDefault();
              setPriorities([...priorities, priorityInput.trim()]);
              setPriorityInput("");
            }
          }}
          className="w-full bg-transparent border-b-2 border-gray-400 outline-none focus:border-blue-500"
        />

        <div className="flex flex-wrap gap-2 mt-3">
          {priorities.map((priority, index) => (
            <div
              key={index}
              className="flex items-center gap-2 px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm"
            >
              <span>{priority}</span>

              <button
                onClick={() =>
                  setPriorities(priorities.filter((_, i) => i !== index))
                }
                className="text-blue-600 hover:text-red-500 "
              >
                ✕
              </button>
            </div>
          ))}
        </div>

        <div className="flex justify-center mt-6">
          <button
            type="button"
            onClick={() => {
              const payload = {
                name,
                profession,
                goal,
                wakeTime,
                sleepTime,
                priorities: priorities.filter((p) => p.trim() !== ""),
              };

              fetch("http://localhost:8080/api/profile/create", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                },
                credentials: "include", // VERY IMPORTANT (session)
                body: JSON.stringify(payload),
              }).then(() => navigate("/HomePage"));

              navigate("/HomePage"); // 👉 go to homepage
            }}
            className="px-10 py-4 bg-blue-500 text-white rounded-4xl border border-black hover:bg-transparent transition-colors"
          >
            Submit
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateAccountForm;
