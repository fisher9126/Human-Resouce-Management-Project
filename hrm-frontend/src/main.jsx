import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ConfigProvider, App as AntApp } from 'antd';
import viVN from 'antd/locale/vi_VN';
import 'antd/dist/reset.css';
import './index.css';

import { AuthProvider } from './context/AuthContext';
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

const theme = {
  token: {
    colorPrimary: '#4f46e5',
    borderRadius: 10,
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
  },
};

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ConfigProvider locale={viVN} theme={theme}>
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
                <Route path="/reports" element={<ProtectedRoute roles={['ROLE_ADMIN','ROLE_MANAGER']}><Reports /></ProtectedRoute>} />
              </Route>
            </Routes>
          </BrowserRouter>
        </AuthProvider>
      </AntApp>
    </ConfigProvider>
  </React.StrictMode>
);
