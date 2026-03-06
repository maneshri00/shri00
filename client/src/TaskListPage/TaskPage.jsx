import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function TaskPage() {
  const [view, setView] = useState("daily");
  const [plan, setPlan] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  // ================= FETCH PLAN =================
  useEffect(() => {
    setLoading(true);
    setError(null);

    const url =
      view === "daily"
        ? "http://localhost:8080/api/plan/daily"
        : "http://localhost:8080/api/plan/weekly";

    fetch(url, { credentials: "include" })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch plan");
        return res.json();
      })
      .then((data) => {
        console.log("PLAN:", data);
        setPlan(data);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [view]);

  // ================= EXTRACT TASKS =================
  const taskList = (() => {
    if (!plan) return [];

    // DAILY → { slots: [] }
    if (plan.slots) return plan.slots;

    // WEEKLY → { days: [ { slots: [] } ] }
    if (plan.days) {
      return plan.days.flatMap((d) => d.slots || []);
    }

    return [];
  })();

  // ================= UI =================
  return (
    <div className="h-screen p-6 bg-gradient-to-br from-black via-gray-900 to-black text-white">

      {/* HEADER */}
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate(-1)}
          className="
            px-4 py-2 rounded-lg
            bg-black/60 backdrop-blur-xl
            border border-sky-400/40
            text-sky-400
            hover:border-sky-300
            hover:shadow-[0_0_20px_rgba(56,189,248,0.8)]
            transition
          "
        >
          ← Back
        </button>

        <h1 className="text-2xl font-semibold text-sky-400 flex-1 text-center">
          Task Planner
        </h1>
      </div>

      {/* TABS */}
      <div className="flex gap-4 mb-6">
        {["daily", "weekly"].map((type) => (
          <button
            key={type}
            onClick={() => setView(type)}
            className={`
              px-5 py-2 rounded-xl
              bg-black/60 backdrop-blur-xl
              border border-sky-400/40
              transition-all
              ${
                view === type
                  ? "border-sky-400 text-sky-400 shadow-[0_0_30px_rgba(56,189,248,0.9)]"
                  : "text-gray-300 hover:border-sky-300"
              }
            `}
          >
            {type === "daily" ? "Daily Plan" : "Weekly Plan"}
          </button>
        ))}
      </div>

      {/* STATUS */}
      {loading && <p className="text-sky-300">Loading plan...</p>}
      {error && <p className="text-red-400">{error}</p>}

      {/* TASK LIST */}
      {!loading && taskList.length > 0 && (
        <div className="space-y-5 overflow-y-auto h-[75vh] pr-2">

          {taskList.map((slot, index) => (
            <div
              key={index}
              className="
                rounded-xl
                bg-black/70
                backdrop-blur-xl
                border border-sky-400/50
                p-4
                hover:border-sky-300
                transition
              "
            >
              <h3 className="text-lg font-semibold text-white">
                {slot.task?.title || "Untitled Task"}
              </h3>

              {slot.task?.description && (
                <p className="text-sm text-gray-400 mt-1">
                  {slot.task.description}
                </p>
              )}

              <p className="text-xs text-sky-400 mt-2">
                {slot.start} → {slot.end}
              </p>
            </div>
          ))}

        </div>
      )}

      {/* EMPTY */}
      {!loading && taskList.length === 0 && (
        <p className="text-gray-400">No tasks in plan</p>
      )}
    </div>
  );
}

export default TaskPage;
