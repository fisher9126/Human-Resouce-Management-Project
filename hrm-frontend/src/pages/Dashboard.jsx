import { useEffect, useState } from 'react';
import { Row, Col, Card, Spin, Typography, Empty } from 'antd';
import { TeamOutlined, ApartmentOutlined, CalendarOutlined, DollarOutlined } from '@ant-design/icons';
import { Column } from '@ant-design/plots';
import { motion } from 'framer-motion';
import CountUp from 'react-countup';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import useScrollReveal from '../context/useScrollReveal';

const { Title, Text } = Typography;

function StatCard({ icon, value, label, cls, delay, isMoney }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
      transition={{ delay, duration: 0.4 }}>
      <div className={`stat-card ${cls}`}>
        <div className="stat-icon">{icon}</div>
        <div className="stat-value">
          <CountUp end={value || 0} duration={1.6} separator="," />
          {isMoney && ' đ'}
        </div>
        <div className="stat-label">{label}</div>
      </div>
    </motion.div>
  );
}

export default function Dashboard() {
  const { user, hasRole } = useAuth();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({});
  const [byDept, setByDept] = useState([]);
  const now = new Date();
  useScrollReveal([loading, byDept.length]);

  useEffect(() => {
    if (!hasRole('ROLE_ADMIN', 'ROLE_MANAGER')) { setLoading(false); return; }
    (async () => {
      try {
        const [dash, dept, pay] = await Promise.all([
          api.get('/reports/dashboard'),
          api.get('/reports/employees-by-department'),
          api.get(`/reports/payroll-summary?thang=${now.getMonth() + 1}&nam=${now.getFullYear()}`),
        ]);
        setStats({ ...dash.data.data, tongQuyLuong: pay.data.data.tongQuyLuong });
        setByDept(dept.data.data.map((d) => ({ phongBan: d.phongBan, soNhanVien: d.soNhanVien })));
      } catch (e) { /* ignore */ }
      finally { setLoading(false); }
    })();
  }, []);

  if (loading) return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>;

  if (!hasRole('ROLE_ADMIN', 'ROLE_MANAGER')) {
    return (
      <Card className="page-fade">
        <Title level={3}>Xin chào, {user?.hoTen} 👋</Title>
        <Text type="secondary">Chúc bạn một ngày làm việc hiệu quả. Dùng menu bên trái để chấm công, gửi đơn nghỉ và xem phiếu lương.</Text>
      </Card>
    );
  }

  const chartConfig = {
    data: byDept,
    xField: 'phongBan',
    yField: 'soNhanVien',
    height: 340,
    columnWidthRatio: 0.5,
    // Cot bo goc tren + mau gradient tim
    columnStyle: { radius: [10, 10, 0, 0], fill: 'l(270) 0:#a5b4fc 1:#4f46e5' },
    // So can giua cot
    label: {
      position: 'middle',
      style: { fill: '#fff', fontSize: 15, fontWeight: 700 },
    },
    xAxis: { label: { style: { fontSize: 12 } } },
    // Animation cham lai cho dang cap
    animation: { appear: { animation: 'grow-in-y', duration: 1800, easing: 'easeQuadOut' } },
    interaction: { elementHighlight: true },
  };

  return (
    <div>
      <Title level={3} className="grad-title" style={{ marginBottom: 20 }}>
        {hasRole('ROLE_MANAGER') && stats.tenPhongBan
          ? `Tổng quan phòng ${stats.tenPhongBan}`
          : 'Tổng quan hệ thống'}
      </Title>
      <Row gutter={[20, 20]}>
        <Col xs={24} sm={12} lg={6}>
          <StatCard icon={<TeamOutlined />} value={stats.tongNhanVien} label="Tổng nhân viên" cls="grad-indigo" delay={0.05} />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatCard icon={<ApartmentOutlined />} value={stats.tongPhongBan} label="Phòng ban" cls="grad-violet" delay={0.15} />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatCard icon={<CalendarOutlined />} value={stats.donNghiChoDuyet} label="Đơn nghỉ chờ duyệt" cls="grad-cyan" delay={0.25} />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatCard icon={<DollarOutlined />} value={Number(stats.tongQuyLuong)} label="Quỹ lương tháng này" cls="grad-amber" delay={0.35} isMoney />
        </Col>
      </Row>

      <div className="reveal" style={{ marginTop: 24 }}>
        <Card title="Cơ cấu nhân viên theo phòng ban" style={{ borderRadius: 16 }}>
          {byDept.length ? <Column {...chartConfig} /> : <Empty description="Chưa có dữ liệu" />}
        </Card>
      </div>
    </div>
  );
}
