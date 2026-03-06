import React, { useEffect, useState } from "react";

function EisenhowerMatrix({ workspaceType }) {
  const [matrix, setMatrix] = useState({
    Q1: [],
    Q2: [],
    Q3: [],
    Q4: [],
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    const url =
      workspaceType === "professional"
        ? "http://localhost:8080/tasks?workspace_id=professional"
        : "http://localhost:8080/tasks?workspace_id=personal";
    fetch(url, { credentials: "include" })
      .then((res) => (res.ok ? res.json() : []))
      .then((tasks) => {
        if (cancelled || !Array.isArray(tasks)) return;
        const grouped = {
          Q1: tasks.filter((t) => t.quadrant === "Q1"),
          Q2: tasks.filter((t) => t.quadrant === "Q2"),
          Q3: tasks.filter((t) => t.quadrant === "Q3"),
          Q4: tasks.filter((t) => t.quadrant === "Q4"),
        };
        setMatrix(grouped);
      })
      .catch(() => {
        if (!cancelled) {
          setMatrix({
            Q1: sampleTasks("Q1"),
            Q2: sampleTasks("Q2"),
            Q3: sampleTasks("Q3"),
            Q4: sampleTasks("Q4"),
          });
        }
      })
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true;
    };
  }, [workspaceType]);

  if (loading) return <div className="text-gray-400">Loading matrix...</div>;

  return (
    <div className="grid grid-cols-2 gap-4">
      <Quadrant title="Q1 • Urgent + Important" items={matrix.Q1} accent="from-red-500/40" />
      <Quadrant title="Q2 • Not Urgent + Important" items={matrix.Q2} accent="from-amber-500/40" />
      <Quadrant title="Q3 • Urgent + Not Important" items={matrix.Q3} accent="from-sky-500/40" />
      <Quadrant title="Q4 • Not Urgent + Not Important" items={matrix.Q4} accent="from-emerald-500/40" />
    </div>
  );
}

function Quadrant({ title, items, accent }) {
  return (
    <div
      className={`
        p-3 rounded-xl border border-gray-800
        bg-gradient-to-br ${accent} to-black
      `}
    >
      <div className="text-sm text-gray-300 mb-2">{title}</div>
      <div className="space-y-2">
        {items.length === 0 && (
          <div className="text-gray-500 text-sm">No items</div>
        )}
        {items.map((t) => (
          <div
            key={t.id || t.externalId}
            className="p-3 rounded-lg bg-black/60 border border-gray-700"
          >
            <div className="text-white text-sm font-semibold">{t.title}</div>
            <div className="text-xs text-gray-400">{t.description}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

function sampleTasks(q) {
  return [
    {
      id: `${q}-1`,
      title: q === "Q1" ? "Pay utility bill" : q === "Q2" ? "Plan weekend trip" : q === "Q3" ? "Respond to ping" : "Clean inbox",
      description: "Auto-generated sample task",
    },
  ];
}

export default EisenhowerMatrix;

