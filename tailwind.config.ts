import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./lib/**/*.{js,ts,jsx,tsx,mdx}"
  ],
  theme: {
    extend: {
      colors: {
        panel: "#101418",
        panel2: "#151b21",
        line: "#28313a",
        ink: "#eef4f8",
        muted: "#8fa0ad",
        accent: "#34d399",
        warn: "#f6c177"
      }
    }
  },
  plugins: []
};

export default config;
