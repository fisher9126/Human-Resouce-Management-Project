import { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Tag, theme } from 'antd';
import {
  DashboardOutlined, TeamOutlined, ApartmentOutlined, ClockCircleOutlined,
  CalendarOutlined, DollarOutlined, FileProtectOutlined, BarChartOutlined,
  LogoutOutlined, MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import useIdleTimeout from '../context/useIdleTimeout';

const { Header, Sider, Content } = Layout;

export default function AppLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout, hasRole } = useAuth();
  useIdleTimeout();
  const { token } = theme.useToken();

  const allItems = [
    { key: '/', icon: <DashboardOutlined />, label: 'Tổng quan', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/employees', icon: <TeamOutlined />, label: 'Nhân viên', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/org', icon: <ApartmentOutlined />, label: 'Phòng ban & Chức vụ', roles: ['ROLE_ADMIN'] },
    { key: '/attendance', icon: <ClockCircleOutlined />, label: 'Chấm công', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/leaves', icon: <CalendarOutlined />, label: 'Nghỉ phép', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/payroll', icon: <DollarOutlined />, label: 'Tính lương', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/contracts', icon: <FileProtectOutlined />, label: 'Hợp đồng', roles: ['ROLE_ADMIN'] },
    { key: '/reports', icon: <BarChartOutlined />, label: 'Báo cáo', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
  ];
  const items = allItems.filter((i) => hasRole(...i.roles));

  const roleLabel = {
    ROLE_ADMIN: { text: 'Quản trị viên', color: 'magenta' },
    ROLE_MANAGER: { text: 'Trưởng phòng', color: 'blue' },
    ROLE_EMPLOYEE: { text: 'Nhân viên', color: 'green' },
  }[user?.vaiTro] || { text: user?.vaiTro, color: 'default' };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed} width={230}
        style={{ background: 'linear-gradient(180deg,#4f46e5,#7c3aed)' }}>
        <div className="brand-logo">
          <i className="fa-solid fa-hippo" style={{ fontSize: 24 }}></i>
          {!collapsed && <span>Human Resource Management</span>}
        </div>
        <Menu
          theme="dark" mode="inline"
          selectedKeys={[location.pathname]}
          onClick={({ key }) => navigate(key)}
          style={{ background: 'transparent', borderInlineEnd: 'none' }}
          items={items.map(({ roles, ...rest }) => rest)}
        />
      </Sider>

      <Layout>
        <Header className="app-header" style={{ padding: '0 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div onClick={() => setCollapsed(!collapsed)} style={{ cursor: 'pointer', fontSize: 18 }}>
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>
          <Dropdown
            menu={{ items: [
              { key: 'profile', icon: <UserOutlined />, label: 'Thông tin cá nhân',
                onClick: () => navigate('/profile') },
              { type: 'divider' },
              { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất',
                onClick: () => { logout(); navigate('/login'); } },
            ]}}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
              <Avatar src={user?.imageUrl || undefined} style={{ background: token.colorPrimary }} icon={<UserOutlined />} />
              <div style={{ lineHeight: 1.2 }}>
                <div style={{ fontWeight: 600 }}>{user?.hoTen || user?.username}</div>
                <Tag color={roleLabel.color} style={{ margin: 0, fontSize: 11 }}>{roleLabel.text}</Tag>
              </div>
            </div>
          </Dropdown>
        </Header>

        <Content style={{ margin: 20 }}>
          <motion.div
            key={location.pathname}
            initial={{ opacity: 0, y: 14 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, ease: 'easeOut' }}>
            <Outlet />
          </motion.div>
        </Content>
      </Layout>
    </Layout>
  );
}
