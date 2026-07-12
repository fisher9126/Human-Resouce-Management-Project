import { useEffect, useState } from 'react';
import {
  Card, Tabs, Table, Tag, Button, Modal, Form, Select, DatePicker,
  InputNumber, Input, Space, App, Typography,
} from 'antd';
import { PlusOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;
const { RangePicker } = DatePicker;

const LOAI = {
  PhepNam: 'Phép năm', NghiOm: 'Nghỉ ốm', ThaiSan: 'Thai sản', KhongLuong: 'Không lương',
};
const TT = {
  ChoDuyet: { text: 'Chờ duyệt', color: 'gold' },
  Duyet: { text: 'Đã duyệt', color: 'green' },
  TuChoi: { text: 'Từ chối', color: 'red' },
};

function MyLeaves() {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  const load = () => api.get('/leaves/me').then((r) => setData(r.data.data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const submit = async () => {
    const v = await form.validateFields();
    const [bd, kt] = v.range;
    const soNgay = kt.diff(bd, 'day') + 1;
    try {
      await api.post('/leaves/request', {
        loaiNghi: v.loaiNghi,
        ngayBatDau: bd.format('YYYY-MM-DD'),
        ngayKetThuc: kt.format('YYYY-MM-DD'),
        soNgayNghi: soNgay,
        lyDo: v.lyDo,
      });
      message.success('Gửi đơn thành công'); setOpen(false); form.resetFields(); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const columns = [
    { title: 'Loại', dataIndex: 'loaiNghi', render: (t) => LOAI[t] || t },
    { title: 'Từ ngày', dataIndex: 'ngayBatDau', render: (t) => dayjs(t).format('DD/MM/YYYY') },
    { title: 'Đến ngày', dataIndex: 'ngayKetThuc', render: (t) => dayjs(t).format('DD/MM/YYYY') },
    { title: 'Số ngày', dataIndex: 'soNgayNghi' },
    { title: 'Lý do', dataIndex: 'lyDo', ellipsis: true },
    { title: 'Trạng thái', dataIndex: 'trangThai', render: (t) => <Tag color={TT[t]?.color}>{TT[t]?.text}</Tag> },
    { title: 'Phản hồi', dataIndex: 'phanHoiNguoiDuyet', render: (t) => t || '—' },
  ];

  return (
    <Card style={{ borderRadius: 16 }}
      extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>Tạo đơn nghỉ</Button>}>
      <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 8 }} />
      <Modal title="Tạo đơn xin nghỉ phép" open={open} onOk={submit} onCancel={() => setOpen(false)}
        okText="Gửi đơn" cancelText="Hủy" destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="loaiNghi" label="Loại nghỉ" rules={[{ required: true }]}>
            <Select options={Object.entries(LOAI).map(([k, v]) => ({ value: k, label: v }))} />
          </Form.Item>
          <Form.Item name="range" label="Khoảng thời gian" rules={[{ required: true }]}>
            <RangePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="lyDo" label="Lý do" rules={[{ required: true }]}>
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}

function PendingLeaves() {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [rejectId, setRejectId] = useState(null);
  const [phanHoi, setPhanHoi] = useState('');

  const load = () => api.get('/leaves/pending').then((r) => setData(r.data.data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const approve = async (id) => {
    try { await api.put(`/leaves/approve/${id}`, { duyet: true, phanHoi: 'Đã duyệt' }); message.success('Đã duyệt'); load(); }
    catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };
  const reject = async () => {
    try { await api.put(`/leaves/approve/${rejectId}`, { duyet: false, phanHoi }); message.success('Đã từ chối'); setRejectId(null); setPhanHoi(''); load(); }
    catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const columns = [
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'] },
    { title: 'Loại', dataIndex: 'loaiNghi', render: (t) => LOAI[t] || t },
    { title: 'Từ', dataIndex: 'ngayBatDau', render: (t) => dayjs(t).format('DD/MM') },
    { title: 'Đến', dataIndex: 'ngayKetThuc', render: (t) => dayjs(t).format('DD/MM') },
    { title: 'Số ngày', dataIndex: 'soNgayNghi' },
    { title: 'Lý do', dataIndex: 'lyDo', ellipsis: true },
    { title: 'Duyệt', width: 130, render: (_, r) => (
      <Space>
        <Button size="small" type="primary" icon={<CheckOutlined />} onClick={() => approve(r.id)} />
        <Button size="small" danger icon={<CloseOutlined />} onClick={() => setRejectId(r.id)} />
      </Space>
    )},
  ];

  return (
    <Card style={{ borderRadius: 16 }}>
      <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 8 }} />
      <Modal title="Từ chối đơn" open={!!rejectId} onOk={reject} onCancel={() => setRejectId(null)}
        okText="Xác nhận từ chối" cancelText="Hủy" okButtonProps={{ danger: true }}>
        <Input.TextArea rows={3} placeholder="Nhập lý do từ chối (bắt buộc)"
          value={phanHoi} onChange={(e) => setPhanHoi(e.target.value)} />
      </Modal>
    </Card>
  );
}

export default function Leaves() {
  const { hasRole } = useAuth();
  const tabs = [{ key: 'me', label: 'Đơn của tôi', children: <MyLeaves /> }];
  if (hasRole('ROLE_ADMIN', 'ROLE_MANAGER')) {
    tabs.push({ key: 'pending', label: 'Duyệt đơn', children: <PendingLeaves /> });
  }
  return (
    <div>
      <Title level={3} style={{ marginBottom: 16 }}>Nghỉ phép</Title>
      <Tabs items={tabs} />
    </div>
  );
}
