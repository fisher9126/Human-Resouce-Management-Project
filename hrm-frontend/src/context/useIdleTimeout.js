import { useEffect, useRef } from 'react';
import { App } from 'antd';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';

// Idle timeout 30 phut. Con hoat dong -> tu refresh token. Treo may 30' -> logout.
const IDLE_LIMIT_MS = 30 * 60 * 1000;      // 30 phut khong thao tac -> het han
const REFRESH_EVERY_MS = 5 * 60 * 1000;    // moi 5' gia han token neu con hoat dong

export default function useIdleTimeout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { message } = App.useApp();
  const lastActivity = useRef(Date.now());

  useEffect(() => {
    if (!user) return;

    const bump = () => { lastActivity.current = Date.now(); };
    const events = ['mousemove', 'mousedown', 'keydown', 'scroll', 'touchstart', 'click'];
    events.forEach((e) => window.addEventListener(e, bump));

    // Kiem tra idle moi 30 giay
    const idleTimer = setInterval(() => {
      if (Date.now() - lastActivity.current >= IDLE_LIMIT_MS) {
        logout();
        message.warning('Phiên đăng nhập đã hết hạn do không hoạt động. Vui lòng đăng nhập lại.');
        navigate('/login');
      }
    }, 30 * 1000);

    // Neu con hoat dong trong 5' gan nhat -> gia han token
    const refreshTimer = setInterval(async () => {
      if (Date.now() - lastActivity.current < REFRESH_EVERY_MS) {
        try {
          const res = await api.post('/auth/refresh');
          localStorage.setItem('hrm_token', res.data.data.accessToken);
        } catch { /* token het han -> interceptor 401 se xu ly */ }
      }
    }, REFRESH_EVERY_MS);

    return () => {
      events.forEach((e) => window.removeEventListener(e, bump));
      clearInterval(idleTimer);
      clearInterval(refreshTimer);
    };
  }, [user]);
}
