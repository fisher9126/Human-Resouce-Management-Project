import { useEffect, useState } from 'react';
import { Card, Button, Row, Col, Table, Tag, DatePicker, Space, App, Typography, Statistic } from 'antd';
import { LoginOutlined, LogoutOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import api from '../api/client';

const { Title, Text } = Typography;

export default function Attendance() {
  const { message } = App.useApp();
  const [now, setNow] = useState(dayjs());
  const [thangNam, setThangNam] = useState(dayjs());
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const t = setInterval(() => setNow(dayjs()), 1000);
    return () => clearInterval(t);
  }, []);

  const loadHistory = async () => {
    setLoading(true);
    try {
      const r = await api.get('/attendance/me', {
        params: { thang: thangNam.month() + 1, nam: thangNam.year() },
      });
      setHistory(r.data.data);
    } catch { /* ignore */ } finally { setLoading(false); }
  };
  useEffect(() => { loadHistory(); }, [thangNam]);

  const doCheck = async (type) => {
    try {
      const r = await api.post(`/attendance/${type}`);
      message.success(r.data.message);
      loadHistory();
    } catch (e) { message.warning(e.response?.data?.message || 'Có lỗi'); }
  };

  const columns = [
    { title: 'Ngày', dataIndex: 'ngay', render: (t) => dayjs(t).format('DD/MM/YYYY') },
    { title: 'Giờ vào', dataIndex: 'gioVao', render: (t) => t || '—' },
    { title: 'Giờ ra', dataIndex: 'gioRa', render: (t) => t || '—' },
    { title: 'Số giờ', dataIndex: 'soGioLam', render: (t) => t ? t.toFixed(1) + 'h' : '—' },
    { title: 'Đi trễ', dataIndex: 'diTre', render: (v) => v ? <Tag color="red">Có</Tag> : <Tag color="green">Không</Tag> },
    { title: 'Về sớm', dataIndex: 'veSom', render: (v) => v ? <Tag color="orange">Có</Tag> : <Tag color="green">Không</Tag> },
  ];

  return (
    <div>
      <Title level={3} style={{ marginBottom: 16 }}>Chấm công</Title>
      <Row gutter={[20, 20]}>
        <Col xs={24} md={10}>
          <motion.div initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} transition={{ duration: 0.4 }}>
            <Card style={{ borderRadius: 16, textAlign: 'center',
              background: 'linear-gradient(135deg,#4f46e5,#7c3aed)', color: '#fff' }}>
              <ClockCircleOutlined style={{ fontSize: 40 }} />
              <div style={{ fontSize: 44, fontWeight: 700, margin: '8px 0', fontVariantNumeric: 'tabular-nums' }}>
                {now.format('HH:mm:ss')}
              </div>
              <Text style={{ color: 'rgba(255,255,255,0.9)' }}>{now.format('dddd, DD/MM/YYYY')}</Text>
              <Space style={{ marginTop: 20, width: '100%', justifyContent: 'center' }}>
                <Button size="large" icon={<LoginOutlined />} onClick={() => doCheck('check-in')}>Check-in</Button>
                <Button size="large" icon={<LogoutOutlined />} onClick={() => doCheck('check-out')}>Check-out</Button>
              </Space>
            </Card>
          </motion.div>
        </Col>
        <Col xs={24} md={14}>
          <Card style={{ borderRadius: 16 }}
            title="Lịch sử chấm công"
            extra={<DatePicker picker="month" value={thangNam} onChange={(v) => v && setThangNam(v)} format="MM/YYYY" allowClear={false} />}>
            <Table rowKey="id" size="small" columns={columns} dataSource={history} loading={loading} pagination={{ pageSize: 6 }} />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
