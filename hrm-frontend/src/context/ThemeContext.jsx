import { createContext, useContext, useState, useEffect } from 'react';

const ThemeContext = createContext(null);

export function ThemeProvider({ children }) {
  const [dark, setDark] = useState(() => localStorage.getItem('hrm_theme') === 'dark');

  useEffect(() => {
    localStorage.setItem('hrm_theme', dark ? 'dark' : 'light');
    document.body.style.background = dark ? '#0f1420' : '#f5f7fb';
  }, [dark]);

  const toggle = () => setDark((d) => !d);

  return (
    <ThemeContext.Provider value={{ dark, toggle }}>
      {children}
    </ThemeContext.Provider>
  );
}

export const useThemeMode = () => useContext(ThemeContext);
