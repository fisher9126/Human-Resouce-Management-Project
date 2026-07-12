import { createContext, useContext, useState } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('hrm_user');
    return raw ? JSON.parse(raw) : null;
  });

  const login = async (username, password) => {
    const res = await api.post('/auth/login', { username, password });
    const data = res.data.data;
    localStorage.setItem('hrm_token', data.accessToken);
    localStorage.setItem('hrm_user', JSON.stringify(data.userInfo));
    setUser(data.userInfo);
    return data.userInfo;
  };

  const logout = () => {
    localStorage.removeItem('hrm_token');
    localStorage.removeItem('hrm_user');
    setUser(null);
  };

  // Cap nhat 1 phan thong tin user (vd avatar) -> header doi ngay
  const updateUser = (patch) => {
    setUser((prev) => {
      const next = { ...prev, ...patch };
      localStorage.setItem('hrm_user', JSON.stringify(next));
      return next;
    });
  };

  const hasRole = (...roles) => user && roles.includes(user.vaiTro);

  return (
    <AuthContext.Provider value={{ user, login, logout, hasRole, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
