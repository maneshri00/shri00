import React from "react";
import {
  LineChart,
  Line,
  Area,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const ProgressLineChart = () => {
  const data = [
    { day: "Mon", value: 20 },
    { day: "Tue", value: 28 },
    { day: "Wed", value: 35 },
    { day: "Thu", value: 32 },
    { day: "Fri", value: 40 },
    { day: "Sat", value: 45 },
    { day: "Sun", value: 55 },
  ];

  return (
    <div className="bg-black p-4 rounded-xl w-full">
      <ResponsiveContainer width="100%" height={220}>
        <LineChart
          data={data}
          margin={{ left: 20, right: 10, bottom: 5 }}
        >
          <CartesianGrid
            stroke="rgba(255,255,255,0.12)"
            strokeDasharray="4 10"
            vertical
            horizontal={false}
          />

          <XAxis
            dataKey="day"
            stroke="#8aa0b6"
            tick={{ fontSize: 12 }}
            interval={0}   
          />

          <YAxis hide />

          <Tooltip
            contentStyle={{
              backgroundColor: "#000000",
              border: "1px solid rgba(0,160,255,0.4)",
              color: "#9AFBFF",
            }}
          />

          <defs>
            <linearGradient id="lineExact" x1="0" y1="0" x2="1" y2="0">
              <stop offset="0%" stopColor="#9AFBFF" />
              <stop offset="100%" stopColor="#1FB6FF" />
            </linearGradient>

            <linearGradient id="areaExact" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="rgba(0,160,255,0.45)" />
              <stop offset="100%" stopColor="rgba(0,160,255,0.02)" />
            </linearGradient>
          </defs>

          <Area
            type="monotone"
            dataKey="value"
            stroke="none"
            fill="url(#areaExact)"
          />

          <Line
            type="monotone"
            dataKey="value"
            stroke="url(#lineExact)"
            strokeWidth={2.5}
            dot={false}
            activeDot={false}
            style={{
              filter: "drop-shadow(0 0 12px rgba(0,160,255,1))",
            }}
          />

          <Line
            type="monotone"
            dataKey="value"
            stroke="transparent"
            dot={{
              r: 6,
              fill: "#000000",
              stroke: "#9AFBFF",
              strokeWidth: 2.5,
            }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ProgressLineChart;
