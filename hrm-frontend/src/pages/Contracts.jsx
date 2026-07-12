import { useEffect, useState } from 'react';
import {
  Card, Table, Tag, Alert, Button, Modal, Form, Select, DatePicker,
  InputNumber, Input, App, Typography,
} from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../api/client';

const { Title } = Typography;
const money = (v) => Number(v || 0).toLocaleString('vi-VN') + ' đ';

const LOAI_HD = { ThuViec: 'Thử việc', XacDinhThoiHan: 'Xác định thời hạn', KhongThoiHan: 'Không thời hạn' };
const TT_HD = {
  ConHieuLuc: { text: 'Còn hiệu lực', color: 'green' },
  HetHieuLuc: { text: 'Hết hiệu lực', color: 'default' },
  DaHuy: { text: 'Đã hủy', color: 'red' },
};

export default function Contracts() {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [expiring, setExpiring] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  const load = async () => {
    setLoading(true);
    try {
      const [all, exp] = await Promise.all([api.get('/contracts'), api.get('/contracts/expiring')]);
      setData(all.data.data);
      setExpiring(exp.data.data);
    } catch { /* ignore */ } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);
  useEffect(() => {
    // Lay danh sach nhan vien de chon khi tao hop dong
    api.get('/employees', { params: { page: 0, size: 200 } })
      .then((r) => setEmployees(r.data.data.content)).catch(() => {});
  }, []);

  const submit = async () => {
    const v = await form.validateFields();
    const payload = {
      soHopDong: v.soHopDong,
      loaiHopDong: v.loaiHopDong,
      ngayKy: v.ngayKy.format('YYYY-MM-DD'),
      ngayHieuLuc: v.ngayHieuLuc.format('YYYY-MM-DD'),
      ngayHetHan: v.ngayHetHan ? v.ngayHetHan.format('YYYY-MM-DD') : null,
      luongThoaThuan: v.luongThoaThuan,
      trangThai: 'ConHieuLuc',
    };
    try {
      await api.post(`/contracts/employee/${v.nhanVienId}`, payload);
      message.success('Thêm hợp đồng thành công');
      setOpen(false); form.resetFields(); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const columns = [
    { title: 'Số HĐ', dataIndex: 'soHopDong' },
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'], render: (t) => t || '—' },
    { title: 'Loại', dataIndex: 'loaiHopDong', render: (t) => LOAI_HD[t] || t },
    { title: 'Ngày ký', dataIndex: 'ngayKy', render: (t) => t && dayjs(t).format('DD/MM/YYYY') },
    { title: 'Hết hạn', dataIndex: 'ngayHetHan', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : 'Không xác định' },
    { title: 'Lương thỏa thuận', dataIndex: 'luongThoaThuan', render: money },
    { title: 'Trạng thái', dataIndex: 'trangThai', render: (t) => <Tag color={TT_HD[t]?.color}>{TT_HD[t]?.text}</Tag> },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={3} style={{ margin: 0 }}>Quản lý Hợp đồng</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>Thêm hợp đồng</Button>
      </div>

      {expiring.length > 0 && (
        <Alert type="warning" showIcon style={{ marginBottom: 16, borderRadius: 12 }}
          message={`Có ${expiring.length} hợp đồng sắp hết hạn trong 30 ngày tới`}
          description={expiring.map((h) => `${h.soHopDong} (${h.nhanVien?.hoTen})`).join(', ')} />
      )}

      <Card style={{ borderRadius: 16 }}>
        <Table rowKey="id" columns={columns} dataSource={data} loading={loading} pagination={{ pageSize: 8 }} />
      </Card>

      <Modal title="Thêm hợp đồng" open={open} onOk={submit} onCancel={() => setOpen(false)}
        okText="Lưu" cancelText="Hủy" width={560} destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="nhanVienId" label="Nhân viên" rules={[{ required: true }]}>
            <Select showSearch optionFilterProp="label"
              options={employees.map((e) => ({ value: e.id, label: `${e.hoTen} (${e.id})` }))} />
          </Form.Item>
          <Form.Item name="soHopDong" label="Số hợp đồng" rules={[{ required: true }]}>
            <Input placeholder="VD: 124/2026/HDLD-ABC" />
          </Form.Item>
          <Form.Item name="loaiHopDong" label="Loại hợp đồng" rules={[{ required: true }]}>
            <Select options={Object.entries(LOAI_HD).map(([k, v]) => ({ value: k, label: v }))} />
          </Form.Item>
          <Form.Item name="ngayKy" label="Ngày ký" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="ngayHieuLuc" label="Ngày hiệu lực" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="ngayHetHan" label="Ngày hết hạn (để trống nếu không thời hạn)">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="luongThoaThuan" label="Lương thỏa thuận" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={0} step={1000000}
              formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
