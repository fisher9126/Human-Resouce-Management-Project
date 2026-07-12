import { useEffect, useState } from 'react';
import { Card, Row, Col, DatePicker, Statistic, Empty, Typography, Spin } from 'antd';
import { Column, Pie } from '@ant-design/plots';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import api from '../api/client';

const { Title } = Typography;

export default function Reports() {
  const [byDept, setByDept] = useState([]);
  const [thangNam, setThangNam] = useState(dayjs());
  const [quyLuong, setQuyLuong] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/reports/employees-by-department')
      .then((r) => setByDept(r.data.data.map((d) => ({ phongBan: d.phongBan, soNhanVien: d.soNhanVien }))))
      .catch(() => {}).finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    api.get('/reports/payroll-summary', { params: { thang: thangNam.month() + 1, nam: thangNam.year() } })
      .then((r) => setQuyLuong(r.data.data)).catch(() => {});
  }, [thangNam]);

  if (loading) return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>;

  const colConfig = {
    data: byDept, xField: 'phongBan', yField: 'soNhanVien', height: 340,
    columnWidthRatio: 0.5,
    columnStyle: { radius: [10, 10, 0, 0], fill: 'l(270) 0:#a5b4fc 1:#4f46e5' },
    label: { position: 'middle', style: { fill: '#fff', fontSize: 15, fontWeight: 700 } },
    animation: { appear: { animation: 'grow-in-y', duration: 1800, easing: 'easeQuadOut' } },
    interaction: { elementHighlight: true },
  };
  const pieConfig = {
    data: byDept, angleField: 'soNhanVien', colorField: 'phongBan', height: 340,
    radius: 0.9, innerRadius: 0.55, legend: { position: 'bottom' },
    // So can giua tung lat banh
    label: { text: 'soNhanVien', position: 'inside', style: { fontWeight: 700, fontSize: 15, fill: '#fff' } },
    animation: { appear: { animation: 'wave-in', duration: 1800, easing: 'easeQuadOut' } },
  };


  return (
    <div>
      <Title level={3} style={{ marginBottom: 16 }}>Báo cáo & Thống kê</Title>
      <Row gutter={[20, 20]}>
        <Col xs={24} lg={12}>
          <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.4 }}>
            <Card title="Nhân viên theo phòng ban (cột)" style={{ borderRadius: 16 }}>
              {byDept.length ? <Column {...colConfig} /> : <Empty />}
            </Card>
          </motion.div>
        </Col>
        <Col xs={24} lg={12}>
          <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.4 }}>
            <Card title="Tỷ trọng nhân sự (tròn)" style={{ borderRadius: 16 }}>
              {byDept.length ? <Pie {...pieConfig} /> : <Empty />}
            </Card>
          </motion.div>
        </Col>
        <Col span={24}>
          <Card title="Tổng quỹ lương" style={{ borderRadius: 16 }}
            extra={<DatePicker picker="month" value={thangNam} onChange={(v) => v && setThangNam(v)} format="MM/YYYY" allowClear={false} />}>
            <Row gutter={20}>
              <Col xs={24} sm={8}>
                <Statistic title="Tháng/Năm" value={`${thangNam.month() + 1}/${thangNam.year()}`} />
              </Col>
              <Col xs={24} sm={8}>
                <Statistic title="Tổng quỹ lương" value={Number(quyLuong?.tongQuyLuong || 0)} suffix="đ" />
              </Col>
              <Col xs={24} sm={8}>
                <Statistic title="Số bảng lương" value={quyLuong?.soBangLuong || 0} />
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  );

}