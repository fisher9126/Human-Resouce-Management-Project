import { useEffect, useState } from 'react';
import { Card, Table, Button, DatePicker, InputNumber, Space, Tag, App, Typography, Row, Col } from 'antd';
import { CalculatorOutlined, CheckCircleOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;
const money = (v) => Number(v || 0).toLocaleString('vi-VN') + ' đ';

function AdminPayroll() {
  const { message } = App.useApp();
  const [thangNam, setThangNam] = useState(dayjs());
  const [ngayCong, setNgayCong] = useState(26);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const r = await api.get('/payroll', { params: { thang: thangNam.month() + 1, nam: thangNam.year() } });
      setData(r.data.data);
    } catch { /* ignore */ } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, [thangNam]);

  const tinhLuong = async () => {
    setLoading(true);
    try {
      await api.post('/payroll/calculate', {
        thang: thangNam.month() + 1, nam: thangNam.year(), ngayCongChuan: ngayCong,
      });
      message.success('Đã tính lương (trạng thái Draft)'); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
    finally { setLoading(false); }
  };

  const chot = async (id) => {
    try { await api.put(`/payroll/${id}/approve`); message.success('Đã chốt bảng lương'); load(); }
    catch (e) { message.error('Không chốt được'); }
  };

  const columns = [
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'] },
    { title: 'Lương cơ bản', dataIndex: 'luongCoBan', render: money },
    { title: 'Phụ cấp', dataIndex: 'phuCap', render: money },
    { title: 'Thưởng', dataIndex: 'thuong', render: money },
    { title: 'Bảo hiểm', dataIndex: 'khauTruBaoHiem', render: (v) => <span style={{ color: '#cf1322' }}>-{money(v)}</span> },
    { title: 'Thuế TNCN', dataIndex: 'khauTruThue', render: (v) => <span style={{ color: '#cf1322' }}>-{money(v)}</span> },
    { title: 'Thực nhận', dataIndex: 'thucNhan', render: (v) => <b style={{ color: '#237804' }}>{money(v)}</b> },
    { title: 'Trạng thái', dataIndex: 'trangThai', render: (t) => (
      <Tag color={t === 'DaDuyet' ? 'green' : t === 'DaThanhToan' ? 'blue' : 'gold'}>
        {t === 'DaDuyet' ? 'Đã duyệt' : t === 'DaThanhToan' ? 'Đã thanh toán' : 'Nháp'}</Tag>
    )},
    { title: '', width: 90, render: (_, r) => r.trangThai === 'Draft'
      ? <Button size="small" icon={<CheckCircleOutlined />} onClick={() => chot(r.id)}>Chốt</Button> : null },
  ];

  return (
    <Card style={{ borderRadius: 16 }}>
      <Space style={{ marginBottom: 16 }} wrap>
        <DatePicker picker="month" value={thangNam} onChange={(v) => v && setThangNam(v)} format="MM/YYYY" allowClear={false} />
        <span>Ngày công chuẩn:</span>
        <InputNumber min={1} max={31} value={ngayCong} onChange={setNgayCong} />
        <Button type="primary" icon={<CalculatorOutlined />} onClick={tinhLuong} loading={loading}>Tính lương</Button>
      </Space>
      <Table rowKey="id" size="small" columns={columns} dataSource={data} loading={loading}
        scroll={{ x: 1000 }} pagination={{ pageSize: 8 }} />
    </Card>
  );
}

function MyPayslip() {
  const [data, setData] = useState([]);
  useEffect(() => { api.get('/payroll/me').then((r) => setData(r.data.data)).catch(() => {}); }, []);
  const columns = [
    { title: 'Tháng/Năm', render: (_, r) => `${r.thang}/${r.nam}` },
    { title: 'Lương cơ bản', dataIndex: 'luongCoBan', render: money },
    { title: 'Phụ cấp', dataIndex: 'phuCap', render: money },
    { title: 'Bảo hiểm', dataIndex: 'khauTruBaoHiem', render: (v) => '-' + money(v) },
    { title: 'Thuế', dataIndex: 'khauTruThue', render: (v) => '-' + money(v) },
    { title: 'Thực nhận', dataIndex: 'thucNhan', render: (v) => <b style={{ color: '#237804' }}>{money(v)}</b> },
  ];
  return <Card style={{ borderRadius: 16 }}><Table rowKey="id" columns={columns} dataSource={data} pagination={false} /></Card>;
}

export default function Payroll() {
  const { hasRole } = useAuth();
  return (
    <div>
      <Title level={3} style={{ marginBottom: 16 }}>
        {hasRole('ROLE_ADMIN', 'ROLE_MANAGER') ? 'Tính lương' : 'Phiếu lương của tôi'}
      </Title>
      {hasRole('ROLE_ADMIN', 'ROLE_MANAGER') ? <AdminPayroll /> : <MyPayslip />}
    </div>
  );
}
