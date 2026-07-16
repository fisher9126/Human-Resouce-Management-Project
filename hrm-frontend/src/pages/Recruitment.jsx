import { useEffect, useState } from 'react';
import {
  Tabs, Card, Table, Button, Modal, Form, Input, DatePicker, Select,
  Switch, Tag, Rate, App, Typography, Space, Popconfirm, Avatar,
} from 'antd';
import { PlusOutlined, DeleteOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../api/client';

const { Title } = Typography;

const TT_UV = {
  Moi: { text: 'Mới', color: 'blue' },
  DangXetDuyet: { text: 'Đang xét duyệt', color: 'gold' },
  DaPhongVan: { text: 'Đã phỏng vấn', color: 'purple' },
  DatYeuCau: { text: 'Đạt yêu cầu', color: 'green' },
  TuChoi: { text: 'Từ chối', color: 'red' },
};

function Posts() {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  const load = () => api.get('/recruitment/posts').then((r) => setData(r.data.data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const submit = async () => {
    const v = await form.validateFields();
    try {
      await api.post('/recruitment/posts', {
        maTin: v.maTin, tieuDe: v.tieuDe, diaDiem: v.diaDiem, moTa: v.moTa,
        hieuLuc: v.hieuLuc ?? true,
        ngayDang: v.ngayDang?.format('YYYY-MM-DD'),
        hanNop: v.hanNop?.format('YYYY-MM-DD'),
      });
      message.success('Đăng tin thành công'); setOpen(false); form.resetFields(); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };
  const remove = async (id) => { try { await api.delete(`/recruitment/posts/${id}`); message.success('Đã xóa'); load(); } catch { message.error('Lỗi'); } };

  const columns = [
    { title: 'Mã tin', dataIndex: 'maTin' },
    { title: 'Tiêu đề', dataIndex: 'tieuDe' },
    { title: 'Địa điểm', dataIndex: 'diaDiem' },
    { title: 'Ngày đăng', dataIndex: 'ngayDang', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : '—' },
    { title: 'Hạn nộp', dataIndex: 'hanNop', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : '—' },
    { title: 'Hiệu lực', dataIndex: 'hieuLuc', render: (v) => v ? <Tag color="green">Còn</Tag> : <Tag>Hết</Tag> },
    { title: '', width: 60, render: (_, r) => <Popconfirm title="Xóa?" onConfirm={() => remove(r.id)}><Button size="small" danger icon={<DeleteOutlined />} /></Popconfirm> },
  ];

  return (
    <Card style={{ borderRadius: 16 }} extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>Đăng tin</Button>}>
      <Table rowKey="id" columns={columns} dataSource={data} scroll={{ x: 'max-content' }} pagination={{ pageSize: 8 }} />
      <Modal title="Đăng tin tuyển dụng" open={open} onOk={submit} onCancel={() => setOpen(false)} okText="Lưu" cancelText="Hủy" destroyOnClose>
        <Form form={form} layout="vertical">
          <Form.Item name="maTin" label="Mã tin" rules={[{ required: true }]}><Input placeholder="MT0001" /></Form.Item>
          <Form.Item name="tieuDe" label="Tiêu đề" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="diaDiem" label="Địa điểm"><Input /></Form.Item>
          <Form.Item name="ngayDang" label="Ngày đăng"><DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" /></Form.Item>
          <Form.Item name="hanNop" label="Hạn nộp"><DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" /></Form.Item>
          <Form.Item name="moTa" label="Mô tả"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="hieuLuc" label="Còn hiệu lực" valuePropName="checked" initialValue={true}><Switch /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}

function Candidates() {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [evalId, setEvalId] = useState(null);
  const [evalForm] = Form.useForm();

  const load = () => api.get('/recruitment/candidates').then((r) => setData(r.data.data)).catch(() => {});
  useEffect(() => { load(); }, []);

  const doEval = async () => {
    const v = await evalForm.validateFields();
    try {
      await api.put(`/recruitment/candidates/${evalId}/evaluate`, { diemDanhGia: v.diemDanhGia, trangThai: v.trangThai });
      message.success('Đã cập nhật đánh giá'); setEvalId(null); evalForm.resetFields(); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const columns = [
    { title: 'Họ tên', dataIndex: 'hoTen', render: (t) => <Space><Avatar size="small" icon={<UserOutlined />} />{t}</Space> },
    { title: 'Vị trí ứng tuyển', dataIndex: 'viTriUngTuyen', ellipsis: true },
    { title: 'SĐT', dataIndex: 'sdt' },
    { title: 'Điểm', dataIndex: 'diemDanhGia', render: (v) => v ? `${v}/10` : '—' },
    { title: 'Trạng thái', dataIndex: 'trangThai', render: (t) => <Tag color={TT_UV[t]?.color}>{TT_UV[t]?.text || t}</Tag> },
    { title: '', width: 100, render: (_, r) => <Button size="small" icon={<StarOutlined />} onClick={() => {
      setEvalId(r.id); evalForm.setFieldsValue({ diemDanhGia: r.diemDanhGia, trangThai: r.trangThai });
    }}>Đánh giá</Button> },
  ];

  return (
    <Card style={{ borderRadius: 16 }}>
      <Table rowKey="id" columns={columns} dataSource={data} scroll={{ x: 'max-content' }} pagination={{ pageSize: 8 }} />
      <Modal title="Đánh giá ứng viên" open={!!evalId} onOk={doEval} onCancel={() => setEvalId(null)} okText="Lưu" cancelText="Hủy" destroyOnClose>
        <Form form={evalForm} layout="vertical">
          <Form.Item name="diemDanhGia" label="Điểm đánh giá (1-10)" rules={[{ required: true }]}>
            <Select options={Array.from({ length: 10 }, (_, i) => ({ value: i + 1, label: `${i + 1}/10` }))} />
          </Form.Item>
          <Form.Item name="trangThai" label="Trạng thái hồ sơ" rules={[{ required: true }]}>
            <Select options={Object.entries(TT_UV).map(([k, v]) => ({ value: k, label: v.text }))} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}

export default function Recruitment() {
  return (
    <div>
      <Title level={3} className="grad-title" style={{ marginBottom: 16 }}>Quản lý Tuyển dụng</Title>
      <Tabs items={[
        { key: 'posts', label: 'Tin tuyển dụng', children: <Posts /> },
        { key: 'candidates', label: 'Danh sách ứng viên', children: <Candidates /> },
      ]} />
    </div>
  );
}
