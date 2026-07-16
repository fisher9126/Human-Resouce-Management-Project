import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ConfigProvider, App as AntApp, theme as antdTheme } from 'antd';
import viVN from 'antd/locale/vi_VN';
import 'antd/dist/reset.css';
import './index.css';

import { AuthProvider } from './context/AuthContext';
import { ThemeProvider, useThemeMode } from './context/ThemeContext';
import ProtectedRoute from './components/ProtectedRoute';
import AppLayout from './components/AppLayout';

import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Employees from './pages/Employees';
import Organization from './pages/Organization';
import Attendance from './pages/Attendance';
import Leaves from './pages/Leaves';
import Payroll from './pages/Payroll';
import Contracts from './pages/Contracts';
import Reports from './pages/Reports';
import Profile from './pages/Profile';
import Rewards from './pages/Rewards';
import Appointments from './pages/Appointments';
import Resignations from './pages/Resignations';
import Shifts from './pages/Shifts';
import Schedules from './pages/Schedules';
import Recruitment from './pages/Recruitment';

const baseTheme = {
  token: {
    colorPrimary: '#4f46e5',
    borderRadius: 10,
    fontFamily: "'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
  },
};

function Root() {
  const { dark } = useThemeMode();
  return (
    <ConfigProvider
      locale={viVN}
      theme={{ ...baseTheme, algorithm: dark ? antdTheme.darkAlgorithm : antdTheme.defaultAlgorithm }}>
      <AntApp>
        <AuthProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
                <Route path="/" element={<Dashboard />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/employees" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Employees /></ProtectedRoute>} />
                <Route path="/org" element={<ProtectedRoute roles={['ROLE_ADMIN']}><Organization /></ProtectedRoute>} />
                <Route path="/attendance" element={<Attendance />} />
                <Route path="/leaves" element={<Leaves />} />
                <Route path="/payroll" element={<Payroll />} />
                <Route path="/contracts" element={<ProtectedRoute roles={['ROLE_ADMIN']}><Contracts /></ProtectedRoute>} />
                <Route path="/rewards" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Rewards /></ProtectedRoute>} />
                <Route path="/appointments" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Appointments /></ProtectedRoute>} />
                <Route path="/resignations" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Resignations /></ProtectedRoute>} />
                <Route path="/shifts" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Shifts /></ProtectedRoute>} />
                <Route path="/schedules" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Schedules /></ProtectedRoute>} />
                <Route path="/recruitment" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Recruitment /></ProtectedRoute>} />
                <Route path="/reports" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Reports /></ProtectedRoute>} />
              </Route>
            </Routes>
          </BrowserRouter>
        </AuthProvider>
      </AntApp>
    </ConfigProvider>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ThemeProvider>
      <Root />
    </ThemeProvider>
  </React.StrictMode>
);
