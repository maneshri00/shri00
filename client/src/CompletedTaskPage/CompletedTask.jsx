import React from "react";
import { useNavigate } from "react-router-dom";

const CompletedTask = ({ tasks }) => {
  const navigate = useNavigate();

  // use status instead of completed
  const completedTask = tasks.filter(task => task.status === "DONE");

  return (
    <div className="h-screen bg-black flex flex-col text-rose-50">
      
      {/* TOP BAR */}
      <div className="h-[10%] bg-gray-900 flex items-center px-6">
        <button
          onClick={() => navigate(-1)}
          className="text-sky-400 font-semibold"
        >
          ← Back
        </button>

        <div className="flex-1 text-center text-white font-semibold">
          Completed Tasks
        </div>

        <div className="w-[60px]" />
      </div>

      {/* CONTENT */}
      <div className="h-[90%] flex justify-center items-start pt-6">
        <div
          className="w-full max-w-md overflow-y-auto p-4 hide-scrollbar"
          style={{ scrollbarWidth: "none" }}
        >
          {completedTask.length === 0 && (
            <p className="text-gray-400 text-center">
              No completed tasks yet
            </p>
          )}

          {completedTask.map((task) => (
            <div key={task.id} className="relative mb-6">

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

                {/* COMPLETED LABEL */}
                <span className="absolute bottom-3 right-3 text-xs text-sky-400">
                  ✔ Completed
                </span>
              </div>

            </div>
          ))}
        </div>
      </div>

    </div>
  );
};

export default CompletedTask;
