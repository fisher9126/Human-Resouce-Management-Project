import { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Tag, theme, Drawer, Grid, Button } from 'antd';
import {
  DashboardOutlined, TeamOutlined, ApartmentOutlined, ClockCircleOutlined,
  CalendarOutlined, DollarOutlined, FileProtectOutlined, BarChartOutlined,
  LogoutOutlined, MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined,
  TrophyOutlined, SwapOutlined, UserDeleteOutlined, ScheduleOutlined,
  FieldTimeOutlined, SolutionOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import { useThemeMode } from '../context/ThemeContext';
import useIdleTimeout from '../context/useIdleTimeout';
import NotificationBell from './NotificationBell';
import { SunOutlined, MoonOutlined } from '@ant-design/icons';

const { Header, Sider, Content } = Layout;
const { useBreakpoint } = Grid;

export default function AppLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout, hasRole } = useAuth();
  const { dark, toggle } = useThemeMode();
  useIdleTimeout();
  const { token } = theme.useToken();
  const screens = useBreakpoint();
  const isMobile = !screens.md; // < 768px = mobile/tablet doc

  const allItems = [
    { key: '/', icon: <DashboardOutlined />, label: 'Tổng quan', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/employees', icon: <TeamOutlined />, label: 'Nhân viên', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/org', icon: <ApartmentOutlined />, label: 'Phòng ban & Chức vụ', roles: ['ROLE_ADMIN'] },
    { key: '/rewards', icon: <TrophyOutlined />, label: 'Khen thưởng', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/appointments', icon: <SwapOutlined />, label: 'Bổ nhiệm', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/resignations', icon: <UserDeleteOutlined />, label: 'Thôi việc', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/attendance', icon: <ClockCircleOutlined />, label: 'Chấm công', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/shifts', icon: <FieldTimeOutlined />, label: 'Ca làm việc', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/schedules', icon: <ScheduleOutlined />, label: 'Lịch làm việc', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/leaves', icon: <CalendarOutlined />, label: 'Nghỉ phép', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/payroll', icon: <DollarOutlined />, label: 'Tính lương', roles: ['ROLE_ADMIN','ROLE_MANAGER','ROLE_EMPLOYEE'] },
    { key: '/contracts', icon: <FileProtectOutlined />, label: 'Hợp đồng', roles: ['ROLE_ADMIN'] },
    { key: '/recruitment', icon: <SolutionOutlined />, label: 'Tuyển dụng', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
    { key: '/reports', icon: <BarChartOutlined />, label: 'Báo cáo', roles: ['ROLE_ADMIN','ROLE_MANAGER'] },
  ];
  const items = allItems.filter((i) => hasRole(...i.roles));

  const roleLabel = {
    ROLE_ADMIN: { text: 'Quản trị viên', color: 'magenta' },
    ROLE_MANAGER: { text: 'Trưởng phòng', color: 'blue' },
    ROLE_EMPLOYEE: { text: 'Nhân viên', color: 'green' },
  }[user?.vaiTro] || { text: user?.vaiTro, color: 'default' };

  const menuNode = (
    <>
      <div className="brand-logo">
        <i className="fa-solid fa-hippo" style={{ fontSize: 24 }}></i>
        {(!collapsed || isMobile) && <span>Human Resource Management</span>}
      </div>
      <Menu
        theme="dark" mode="inline"
        selectedKeys={[location.pathname]}
        onClick={({ key }) => { navigate(key); setDrawerOpen(false); }}
        style={{ background: 'transparent', borderInlineEnd: 'none' }}
        items={items.map(({ roles, ...rest }) => rest)}
      />
    </>
  );

  const siderBg = { background: 'linear-gradient(180deg,#4f46e5,#7c3aed)' };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {isMobile ? (
        <Drawer
          placement="left" open={drawerOpen} onClose={() => setDrawerOpen(false)}
          width={230} closable={false}
          styles={{ body: { padding: 0, ...siderBg }, header: { display: 'none' } }}>
          {menuNode}
        </Drawer>
      ) : (
        <Sider trigger={null} collapsible collapsed={collapsed} width={230} style={siderBg}>
          {menuNode}
        </Sider>
      )}

      <Layout>
        <Header className="app-header" style={{ padding: '0 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div onClick={() => isMobile ? setDrawerOpen(true) : setCollapsed(!collapsed)}
            style={{ cursor: 'pointer', fontSize: 18 }}>
            {isMobile ? <MenuUnfoldOutlined /> : (collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />)}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Button type="text" shape="circle" onClick={toggle}
              icon={dark ? <SunOutlined style={{ fontSize: 18 }} /> : <MoonOutlined style={{ fontSize: 18 }} />}
              title={dark ? 'Chế độ sáng' : 'Chế độ tối'} />
            <NotificationBell />
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
              {!isMobile && (
                <div style={{ lineHeight: 1.2 }}>
                  <div style={{ fontWeight: 600 }}>{user?.hoTen || user?.username}</div>
                  <Tag color={roleLabel.color} style={{ margin: 0, fontSize: 11 }}>{roleLabel.text}</Tag>
                </div>
              )}
            </div>
          </Dropdown>
          </div>
        </Header>

        <Content style={{ margin: isMobile ? 12 : 20 }}>
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
