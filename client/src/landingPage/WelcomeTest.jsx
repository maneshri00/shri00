import React, { useEffect, useState } from "react";

const TypingText = () => {
  const text = "Welcome to Tusk. Focus on what matters.";
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (index < text.length) {
      const timer = setTimeout(() => {
        setIndex(index + 1);
      }, 120); // typing speed

      return () => clearTimeout(timer);
    }
  }, [index, text.length]);

  return (
    <span className="text-[#e5e7eb] text-5xl font-sans font-semibold tracking-tight">
      {text.slice(0, index)}
      {index < text.length && <span className="opacity-80">_</span>}
    </span>
  );
};

export default TypingText;
