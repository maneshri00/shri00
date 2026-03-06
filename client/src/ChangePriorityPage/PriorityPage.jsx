import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const PriorityPage = () => {
  const [priorityInput, setPriorityInput] = useState("");
  const [priorities, setPriorities] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  // ================= FETCH PRIORITIES =================
  useEffect(() => {
    setLoading(true);
    setError(null);

    fetch("http://localhost:8080/api/profile/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load priorities");
        return res.json();
      })
      .then((data) => {
        if (data?.priorities) {
          setPriorities(data.priorities);
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  // ================= ADD PRIORITY =================
  const handleAdd = (e) => {
    if (e.key === "Enter" && priorityInput.trim()) {
      e.preventDefault();
      setPriorities([...priorities, priorityInput.trim()]);
      setPriorityInput("");
    }
  };

  // ================= SAVE PRIORITIES =================
  const handleSave = () => {
    setLoading(true);
    setError(null);

    fetch("http://localhost:8080/api/profile/update-priorities", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ priorities }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to save priorities");
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
          Edit Priorities
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
        {/* INPUT */}
        <input
          type="text"
          value={priorityInput}
          onChange={(e) => setPriorityInput(e.target.value)}
          onKeyDown={handleAdd}
          placeholder="Type priority and press Enter"
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

        {/* TAGS */}
        <div className="flex flex-wrap gap-3 mt-4">
          {priorities.map((p, index) => (
            <div
              key={index}
              className="
                flex items-center gap-2
                px-3 py-1 rounded-full
                bg-sky-500/20
                text-sky-300
                border border-sky-400/40
              "
            >
              {p}
              <button
                onClick={() =>
                  setPriorities(priorities.filter((_, i) => i !== index))
                }
                className="text-red-400 hover:text-red-500"
              >
                ✕
              </button>
            </div>
          ))}
        </div>

        {/* ACTIONS */}
        <div className="flex justify-end mt-8">
          <button
            onClick={handleSave}
            className="
              px-6 py-2 rounded-lg
              bg-sky-500
              text-black
              font-medium
              hover:bg-sky-400
              transition
            "
          >
            Save Priorities
          </button>
        </div>
      </div>
    </div>
  );
};

export default PriorityPage;
