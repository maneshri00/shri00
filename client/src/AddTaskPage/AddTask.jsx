import { useState } from "react";
import { useNavigate } from "react-router-dom";

function AddTask() {

  const navigate = useNavigate();

  const [task, setTask] = useState({
    title: "",
    description: "",
    priority: "MEDIUM",
    estimatedMinutes: 30,
    fixed: false,
    fixedStartTime: "",
    fixedEndTime: ""
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setTask({
      ...task,
      [name]: type === "checkbox" ? checked : value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const res = await fetch("http://localhost:8080/tasks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(task)
      });

      if (!res.ok) throw new Error("Failed to add task");

      navigate(-1);   // go back after save
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

return (
  <div className="min-h-screen bg-gradient-to-br from-black via-gray-900 to-black text-white p-6">

    {/* HEADER */}
    <div className="flex items-center gap-4 mb-6">
      <button
        onClick={() => navigate(-1)}
        className="px-4 py-2 rounded-lg border border-sky-400/40 text-sky-400 hover:border-sky-300"
      >
        ← Back
      </button>
    </div>

    {/* CENTER AREA */}
    <div className="flex justify-center items-center min-h-[80vh]">

      <div className="relative">
        <div className="absolute -inset-1 bg-gradient-to-r from-sky-400 via-cyan-500 to-blue-600 rounded-2xl blur opacity-40"></div>

        <form
          onSubmit={handleSubmit}
          className="
            relative
            w-[380px]
            bg-black/70
            backdrop-blur-xl
            border border-sky-400/40
            rounded-2xl
            p-8
            space-y-4
            shadow-2xl
          "
        >

          {/* FORM TITLE */}
          <h2 className="text-center text-2xl font-semibold text-sky-400 mb-4">
            Add New Task
          </h2>

          <input
            name="title"
            placeholder="Task title"
            value={task.title}
            onChange={handleChange}
            required
            className="w-full p-2 rounded bg-gray-800 outline-none focus:ring-2 focus:ring-sky-400"
          />

          {/* rest of your inputs remain unchanged */}


          <textarea
            name="description"
            placeholder="Description"
            value={task.description}
            onChange={handleChange}
            className="w-full p-2 rounded bg-gray-800 outline-none focus:ring-2 focus:ring-sky-400"
          />

          <select
            name="priority"
            value={task.priority}
            onChange={handleChange}
            className="w-full p-2 rounded bg-gray-800 focus:ring-2 focus:ring-sky-400"
          >
            <option>LOW</option>
            <option>MEDIUM</option>
            <option>HIGH</option>
          </select>

          <input
            type="number"
            name="estimatedMinutes"
            value={task.estimatedMinutes}
            onChange={handleChange}
            min="5"
            className="w-full p-2 rounded bg-gray-800 focus:ring-2 focus:ring-sky-400"
          />

          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              name="fixed"
              checked={task.fixed}
              onChange={handleChange}
              className="accent-sky-400"
            />
            Fixed Time Task
          </label>

          {task.fixed && (
            <div className="flex gap-2">
              <input
                type="time"
                name="fixedStartTime"
                value={task.fixedStartTime}
                onChange={handleChange}
                className="w-1/2 p-2 rounded bg-gray-800 focus:ring-2 focus:ring-sky-400"
              />

              <input
                type="time"
                name="fixedEndTime"
                value={task.fixedEndTime}
                onChange={handleChange}
                className="w-1/2 p-2 rounded bg-gray-800 focus:ring-2 focus:ring-sky-400"
              />
            </div>
          )}

          {error && <p className="text-red-400">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className="
              w-full
              py-2
              rounded-lg
              bg-sky-500
              text-black
              font-semibold
              hover:bg-sky-400
              transition
              shadow-lg shadow-sky-500/30
            "
          >
            {loading ? "Saving..." : "Add Task"}
          </button>

        </form>
      </div>

    </div>
  </div>
);
}

export default AddTask;
