import { useEffect, useState } from 'react';
import {
  Card, Table, Button, Input, Select, Space, Tag, Modal, Form, DatePicker,
  Popconfirm, App, Row, Col, Typography, Avatar,
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UserOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;

const TRANG_THAI = {
  DangLamViec: { text: 'Đang làm việc', color: 'green' },
  NghiThaiSan: { text: 'Nghỉ thai sản', color: 'orange' },
  DaNghiViec: { text: 'Đã nghỉ việc', color: 'red' },
};

export default function Employees() {
  const { hasRole } = useAuth();
  const { message } = App.useApp();
  const isAdmin = hasRole('ROLE_ADMIN');
  const isManager = hasRole('ROLE_MANAGER');
  const canEdit = isAdmin || isManager; // ca admin va manager deu CRUD duoc

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [phongBanId, setPhongBanId] = useState(null);
  const [vaiTro, setVaiTro] = useState(null);
  const [depts, setDepts] = useState([]);
  const [positions, setPositions] = useState([]);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    setLoading(true);
    try {
      const res = await api.get('/employees', { params: { keyword, phongBanId, vaiTro, page, size: 8 } });
      setData(res.data.data.content);
      setTotal(res.data.data.totalElements);
    } catch (e) { message.error('Không tải được danh sách'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [page, keyword, phongBanId, vaiTro]);
  useEffect(() => {
    api.get('/departments').then((r) => setDepts(r.data.data)).catch(() => {});
    api.get('/positions').then((r) => setPositions(r.data.data)).catch(() => {});
  }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (record) => {
    setEditing(record);
    form.setFieldsValue({
      ...record,
      ngaySinh: record.ngaySinh ? dayjs(record.ngaySinh) : null,
      ngayVaoLam: record.ngayVaoLam ? dayjs(record.ngayVaoLam) : null,
      phongBanId: record.phongBan?.id,
      chucVuId: record.chucVu?.id,
    });
    setModalOpen(true);
  };

  const submit = async () => {
    const v = await form.validateFields();
    const payload = {
      ...v,
      ngaySinh: v.ngaySinh ? v.ngaySinh.format('YYYY-MM-DD') : null,
      ngayVaoLam: v.ngayVaoLam ? v.ngayVaoLam.format('YYYY-MM-DD') : null,
    };
    try {
      if (editing) { await api.put(`/employees/${editing.id}`, payload); message.success('Cập nhật thành công'); }
      else { await api.post('/employees', payload); message.success('Thêm nhân viên thành công'); }
      setModalOpen(false); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi xảy ra'); }
  };

  const remove = async (id) => {
    try { await api.delete(`/employees/${id}`); message.success('Đã vô hiệu hóa'); load(); }
    catch (e) { message.error('Không xóa được'); }
  };

  const columns = [
    { title: 'Mã', dataIndex: 'id', width: 60 },
    { title: 'Họ tên', dataIndex: 'hoTen', render: (t) => (
      <Space><Avatar size="small" icon={<UserOutlined />} />{t}</Space>) },
    { title: 'Phòng ban', dataIndex: ['phongBan', 'tenPhongBan'], render: (t) => t || '—' },
    { title: 'Chức vụ', dataIndex: ['chucVu', 'tenChucVu'], render: (t) => t || '—' },
    { title: 'SĐT', dataIndex: 'sdt' },
    { title: 'Trạng thái', dataIndex: 'trangThai', render: (t) => {
      const s = TRANG_THAI[t] || { text: t, color: 'default' };
      return <Tag color={s.color}>{s.text}</Tag>;
    }},
    ...(isAdmin ? [{
      title: 'Thao tác', width: 130, render: (_, r) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(r)} />
          <Popconfirm title="Vô hiệu hóa nhân viên này?" onConfirm={() => remove(r.id)}>
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    }] : []),
  ];

  return (
    <div>
      <Row justify="space-between" align="middle" style={{ marginBottom: 16 }}>
        <Title level={3} className="grad-title" style={{ margin: 0 }}>Quản lý Nhân viên</Title>
        {canEdit && <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Thêm nhân viên</Button>}
      </Row>

      <Card style={{ borderRadius: 16 }}>
        <Space style={{ marginBottom: 16 }} wrap>
          <Input.Search placeholder="Tìm theo tên / SĐT / mã" allowClear
            prefix={<SearchOutlined />} style={{ width: 260 }}
            onSearch={(v) => { setPage(0); setKeyword(v); }} />
          {isAdmin && (
            <Select placeholder="Lọc phòng ban" allowClear style={{ width: 200 }}
              onChange={(v) => { setPage(0); setPhongBanId(v || null); }}
              options={depts.map((d) => ({ value: d.id, label: d.tenPhongBan }))} />
          )}
          {isAdmin && (
            <Select placeholder="Lọc vai trò" allowClear style={{ width: 180 }}
              onChange={(v) => { setPage(0); setVaiTro(v || null); }}
              options={[
                { value: 'ROLE_ADMIN', label: 'Quản trị viên' },
                { value: 'ROLE_MANAGER', label: 'Trưởng phòng' },
                { value: 'ROLE_EMPLOYEE', label: 'Nhân viên' },
              ]} />
          )}
        </Space>

        <Table
          rowKey="id" columns={columns} dataSource={data} loading={loading}
          scroll={{ x: 'max-content' }}
          pagination={{ current: page + 1, pageSize: 8, total, onChange: (p) => setPage(p - 1) }}
        />
      </Card>

      <Modal
        title={editing ? 'Sửa nhân viên' : 'Thêm nhân viên'}
        open={modalOpen} onOk={submit} onCancel={() => setModalOpen(false)}
        width={640} okText="Lưu" cancelText="Hủy" destroyOnClose>
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}><Form.Item name="hoTen" label="Họ tên" rules={[{ required: true }]}><Input /></Form.Item></Col>
            <Col span={12}><Form.Item name="gioiTinh" label="Giới tính">
              <Select options={[{ value: 'Nam', label: 'Nam' }, { value: 'Nữ', label: 'Nữ' }]} /></Form.Item></Col>
            <Col span={12}><Form.Item name="cccd" label="CCCD" rules={[{ required: true }]}><Input /></Form.Item></Col>
            <Col span={12}><Form.Item name="sdt" label="SĐT" rules={[{ required: true }]}><Input /></Form.Item></Col>
            <Col span={12}><Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}><Input /></Form.Item></Col>
            <Col span={12}><Form.Item name="ngaySinh" label="Ngày sinh"><DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" /></Form.Item></Col>
            <Col span={24}><Form.Item name="diaChi" label="Địa chỉ"><Input /></Form.Item></Col>
            <Col span={8}><Form.Item name="phongBanId" label="Phòng ban">
              <Select options={depts.map((d) => ({ value: d.id, label: d.tenPhongBan }))} /></Form.Item></Col>
            <Col span={8}><Form.Item name="chucVuId" label="Chức vụ">
              <Select options={positions.map((p) => ({ value: p.id, label: p.tenChucVu }))} /></Form.Item></Col>
            <Col span={8}><Form.Item name="ngayVaoLam" label="Ngày vào làm"><DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" /></Form.Item></Col>
            {editing && <Col span={12}><Form.Item name="trangThai" label="Trạng thái">
              <Select options={Object.entries(TRANG_THAI).map(([k, v]) => ({ value: k, label: v.text }))} /></Form.Item></Col>}
          </Row>
        </Form>
      </Modal>
    </div>
  );
}
