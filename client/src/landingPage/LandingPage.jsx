import React, { useEffect, useState } from "react";
import WelcomeTest from "./WelcomeTest";
import glow from "./glow.png";
import glow1 from "./glow2.png";
import { motion, useAnimationControls } from "framer-motion";
import gmaillogo from "./gmaillogo.png";
import googlecalendar from "./googlecalendar.png";
import notionicon from "./notionicon.jpg";
import digital1 from "./digital.png";
import digital2 from "./support247.png";
import digital3 from "./confused.png";
import { useNavigate } from "react-router-dom";
import graph from "./graph.png";
import { fetchUserStatus } from "../services/api.js";

const LandingPage = () => {
  const navigate = useNavigate();
  const [status, setStatus] = useState(null);

  // Animation controls for strict sequencing
  const digital3Controls = useAnimationControls();
  const digital2Controls = useAnimationControls();
  const digital1Controls = useAnimationControls();

  // Run animations strictly one after another
  useEffect(() => {
    const runSequence = async () => {
      await digital3Controls.start({
        x: 0,
        opacity: 1,
        transition: { duration: 0.9, ease: "easeOut" },
      });

      await digital2Controls.start({
        x: 0,
        opacity: 1,
        transition: { duration: 0.9, ease: "easeOut" },
      });

      await digital1Controls.start({
        x: 0,
        opacity: 1,
        transition: { duration: 0.9, ease: "easeOut" },
      });
    };

    runSequence();
  }, []);
  useEffect(() => {
    let cancelled = false;
    (async () => {
      const s = await fetchUserStatus();
      if (!cancelled) {
        window.__status = s;
        setStatus(s);
      }
    })();
    return () => { cancelled = true; };
  }, []);

  return (
    <div className="min-h-screen grid grid-rows-[10%_70%_20%] bg-black w-screen h-screen">

     
      <div className="flex items-center justify-center">
        <div className="font-[Onest] font-normal text-2xl tracking-tight text-[#e5e7eb]">
          Tusk
        </div>
      </div>

      <div className="mt-8 relative w-full h-[520px]">
        <div className="flex w-full h-full">

          {/* LEFT SIDE */}
          <div className="relative w-[50%] h-full flex items-center justify-center">

            <motion.img
              src={glow}
              className="absolute inset-0 w-full h-full"
              initial={{ opacity: 0.6, y: 30, scale: 0.98 }}
              animate={{
                opacity: [0.6, 0.8, 0.6],
                y: [30, -20, 30],
                scale: [0.98, 1.05, 0.98],
              }}
              transition={{
                duration: 14,
                repeat: Infinity,
                ease: "easeInOut",
              }}
            />

            {/* DIGITAL IMAGES */}
            <div className="relative z-10 flex flex-col items-center gap-6 mt-75 -translate-x-30">

              {/* TOP ROW */}
              <div className="flex gap-4">

                <motion.img
                  src={digital2}
                  alt="Digital 3"
                  className="w-[250px] object-contain rounded-3xl"
                  initial={{ x: -120, opacity: 0 }}
                  animate={digital3Controls}
                />

                <motion.img
                  src={digital3}
                  alt="Digital 2"
                  className="w-[250px] object-contain rounded-3xl"
                  initial={{ x: -120, opacity: 0 }}
                  animate={digital2Controls}
                />

              </div>

              <motion.img
                src={digital1}
                alt="Digital 1"
                className="w-[50%] object-contain rounded-4xl border-2 border-sky-300"
                initial={{ x: -120, opacity: 0 }}
                animate={digital1Controls}
              />

            </div>
          </div>

          {/* RIGHT SIDE */}
          <div className="relative w-[30%] h-full ml-auto flex items-center justify-center">

            {/* Right Glow (unchanged) */}
            <motion.img
              src={glow1}
              className="absolute inset-0 w-full h-full"
              initial={{ opacity: 0.55, y: -30, scale: 0.96 }}
              animate={{
                opacity: [0.55, 0.75, 0.55],
                y: [-30, 20, -30],
                scale: [0.96, 1.03, 0.96],
              }}
              transition={{
                duration: 16,
                repeat: Infinity,
                ease: "easeInOut",
              }}
            />

            {/* GRAPH (from right, unchanged) */}
            <motion.img
              src={graph}
              alt="graph"
              className="relative z-10 w-[72%] object-contain rounded-1xl mt-75"
              initial={{ x: 120, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              transition={{
                duration: 1.2,
                ease: "easeOut",
                delay: 0.4,
              }}
            />

          </div>
        </div>

        {/* CENTER CONTENT */}
        <div className="absolute inset-0 flex flex-col items-center z-10">
          <WelcomeTest />

          <div className="mt-20 text-amber-50 text-center w-3xl font-serif text-2xl">
            Organize your tasks, set clear priorities, and stay focused on what
            truly matters. Our web app helps you plan smarter, reduce confusion,
            and turn daily goals into consistent progress.
          </div>

          <div className="mt-12 flex gap-6">
            <button
              onClick={() => {
                try { localStorage.setItem("workspacePreferred", "personal"); } catch {}
                navigate("/continue?workspace=personal");
              }}
              className="
                bg-sky-600 text-white px-6 py-3 rounded-lg font-semibold
                hover:bg-sky-500 transition-colors
              "
            >
              Explore Personal
            </button>
            <button
              onClick={() => {
                try { localStorage.setItem("workspacePreferred", "professional"); } catch {}
                navigate("/continue?workspace=professional");
              }}
              className="
                bg-emerald-600 text-white px-6 py-3 rounded-lg font-semibold
                hover:bg-emerald-500 transition-colors
              "
            >
              Explore Professional
            </button>
          </div>
        </div>
      </div>

      {/* ================= FOOTER ================= */}
      <div className="flex items-center justify-center">
        <div className="font-[DM Sans] font-medium text-lg tracking-tight text-[#e5e7eb] text-center">
          <div className="relative z-40">
            Integrated with your favorite tools:
          </div>
          <div className="mt-3 flex items-center justify-center gap-4">
            <img src={gmaillogo} alt="Gmail" className="w-6 h-6" />
            <img src={googlecalendar} alt="Calendar" className="w-6 h-6" />
            <img src={notionicon} alt="Notion" className="w-6 h-6" />
          </div>
        </div>
      </div>

    </div>
  );
};

export default LandingPage;
