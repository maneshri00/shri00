import { Circle } from "rc-progress";

const NeonProgress = ({ percent }) => {
  return (
    <div
      className="relative w-[180px] h-[180px] flex items-center justify-center rounded-full bg-black"
      style={{
        boxShadow: `
          0 0 20px rgba(56,189,248,0.6),
          0 0 60px rgba(56,189,248,0.35),
          0 0 120px rgba(56,189,248,0.2)
        `,
      }}
    >
      <Circle
        percent={percent}
        strokeWidth={1}
        trailWidth={1}
        strokeColor={{
          "0%": "#f0fcff",
          "100%": "#7dd3fc",
        }}
        trailColor="#000000"
        strokeLinecap="round"
      />

      <div className="absolute z-20 text-center">
        <p className="text-3xl font-bold text-sky-400">
          {percent}%
        </p>
        <p className="text-[11px] tracking-widest text-sky-300/70">
          Productivity
        </p>
      </div>
    </div>
  );
};

export default NeonProgress;
