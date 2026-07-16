import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Select, Input, DatePicker, App, Typography, Tag, Space, Popconfirm } from 'antd';
import { PlusOutlined, DeleteOutlined, CheckOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../api/client';
import useEmployees from '../context/useEmployees';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;

export default function Resignations() {
  const { message } = App.useApp();
  const { hasRole } = useAuth();
  const employees = useEmployees();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();
  const isAdmin = hasRole('ROLE_ADMIN');

  const load = async () => {
    setLoading(true);
    try { const r = await api.get('/resignations'); setData(r.data.data); }
    catch { message.error('Không tải được'); } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const submit = async () => {
    const v = await form.validateFields();
    try {
      await api.post('/resignations', { ...v, ngayThoiViec: v.ngayThoiViec?.format('YYYY-MM-DD') });
      message.success('Thêm thành công'); setOpen(false); form.resetFields(); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const approve = async (id) => {
    try { await api.put(`/resignations/${id}/approve`); message.success('Đã duyệt thôi việc'); load(); }
    catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const remove = async (id) => {
    try { await api.delete(`/resignations/${id}`); message.success('Đã xóa'); load(); }
    catch { message.error('Không xóa được'); }
  };

  const columns = [
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'], render: (t) => t || '—' },
    { title: 'Ngày thôi việc', dataIndex: 'ngayThoiViec', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : '—' },
    { title: 'Lý do', dataIndex: 'lyDo', ellipsis: true },
    { title: 'Trạng thái', dataIndex: 'trangThai', render: (t) => (
      <Tag color={t === 'DaDuyet' ? 'green' : 'gold'}>{t === 'DaDuyet' ? 'Đã duyệt' : 'Chờ duyệt'}</Tag>
    )},
    ...(isAdmin ? [{ title: 'Thao tác', width: 130, render: (_, r) => (
      <Space>
        {r.trangThai !== 'DaDuyet' && <Button size="small" type="primary" icon={<CheckOutlined />} onClick={() => approve(r.id)}>Duyệt</Button>}
        <Popconfirm title="Xóa?" onConfirm={() => remove(r.id)}><Button size="small" danger icon={<DeleteOutlined />} /></Popconfirm>
      </Space>
    )}] : []),
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={3} className="grad-title" style={{ margin: 0 }}>Quản lý Thôi việc</Title>
        {isAdmin && <Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>Thêm mới</Button>}
      </div>
      <Card style={{ borderRadius: 16 }}>
        <Table rowKey="id" columns={columns} dataSource={data} loading={loading} scroll={{ x: 'max-content' }} pagination={{ pageSize: 8 }} />
      </Card>
      <Modal title="Thêm thôi việc" open={open} onOk={submit} onCancel={() => setOpen(false)} okText="Lưu" cancelText="Hủy" destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="nhanVienId" label="Nhân viên" rules={[{ required: true }]}>
            <Select showSearch optionFilterProp="label" options={employees} />
          </Form.Item>
          <Form.Item name="ngayThoiViec" label="Ngày thôi việc" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="lyDo" label="Lý do" rules={[{ required: true }]}><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
