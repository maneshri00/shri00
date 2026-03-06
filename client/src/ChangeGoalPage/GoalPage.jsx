import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function GoalPage() {
  const [goal, setGoal] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  // ================= FETCH GOAL =================
  useEffect(() => {
    setLoading(true);
    setError(null);

    fetch("http://localhost:8080/api/profile/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load goal");
        return res.json();
      })
      .then((data) => {
        if (data?.longTermGoal) {
          setGoal(data.longTermGoal);
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  // ================= SAVE GOAL =================
  const saveGoal = () => {
    setLoading(true);
    setError(null);

    fetch("http://localhost:8080/api/profile/goal", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ goal }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to save goal");
        navigate(-1);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

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
          Edit Goal
        </h1>
      </div>

      {/* STATUS */}
      {loading && <p className="text-sky-300">Loading...</p>}
      {error && <p className="text-red-400">{error}</p>}

      {/* CARD */}
      <div
        className="
          max-w-xl mx-auto
          rounded-xl
          bg-black/70
          backdrop-blur-xl
          border border-sky-400/50
          p-6
          hover:border-sky-300
          transition
        "
      >
        <label className="block text-sm text-gray-400 mb-2">
          Your Long Term Goal
        </label>

        <input
          type="text"
          value={goal}
          onChange={(e) => setGoal(e.target.value)}
          placeholder="Enter your main goal"
          className="
            w-full
            bg-transparent
            border-b-2 border-gray-600
            outline-none
            focus:border-sky-400
            pb-2
            text-white
          "
        />

        {/* ACTIONS */}
        <div className="flex justify-end mt-6 gap-4">
          <button
            onClick={() => navigate(-1)}
            className="
              px-5 py-2 rounded-lg
              bg-black/60 backdrop-blur-xl
              border border-gray-500
              text-gray-300
              hover:border-gray-300
              transition
            "
          >
            Cancel
          </button>

          <button
            onClick={saveGoal}
            className="
              px-6 py-2 rounded-lg
              bg-sky-500
              text-black
              font-medium
              hover:bg-sky-400
              transition
            "
          >
            Save Goal
          </button>
        </div>
      </div>

    </div>
  );
}

export default GoalPage;
