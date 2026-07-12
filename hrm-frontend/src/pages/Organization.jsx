import { useEffect, useState } from 'react';
import { Tabs, Card, Table, Button, Modal, Form, Input, InputNumber, Space, Popconfirm, App, Typography } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import api from '../api/client';

const { Title } = Typography;

function CrudPanel({ endpoint, columns, formFields, title }) {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    setLoading(true);
    try { const r = await api.get(endpoint); setData(r.data.data); }
    catch { message.error('Không tải được'); } finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setOpen(true); };
  const openEdit = (r) => { setEditing(r); form.setFieldsValue(r); setOpen(true); };

  const submit = async () => {
    const v = await form.validateFields();
    try {
      if (editing) await api.put(`${endpoint}/${editing.id}`, v);
      else await api.post(endpoint, v);
      message.success('Lưu thành công'); setOpen(false); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };
  const remove = async (id) => {
    try { await api.delete(`${endpoint}/${id}`); message.success('Đã xóa'); load(); }
    catch { message.error('Không xóa được (có thể đang được dùng)'); }
  };

  const cols = [
    ...columns,
    { title: 'Thao tác', width: 120, render: (_, r) => (
      <Space>
        <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(r)} />
        <Popconfirm title="Xóa mục này?" onConfirm={() => remove(r.id)}>
          <Button size="small" danger icon={<DeleteOutlined />} />
        </Popconfirm>
      </Space>
    )},
  ];

  return (
    <Card style={{ borderRadius: 16 }}
      extra={<Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Thêm {title}</Button>}>
      <Table rowKey="id" columns={cols} dataSource={data} loading={loading} pagination={{ pageSize: 8 }} />
      <Modal title={editing ? `Sửa ${title}` : `Thêm ${title}`} open={open}
        onOk={submit} onCancel={() => setOpen(false)} okText="Lưu" cancelText="Hủy" destroyOnClose>
        <Form form={form} layout="vertical">{formFields}</Form>
      </Modal>
    </Card>
  );
}

export default function Organization() {
  return (
    <div>
      <Title level={3} style={{ marginBottom: 16 }}>Phòng ban & Chức vụ</Title>
      <Tabs items={[
        {
          key: 'dept', label: 'Phòng ban',
          children: <CrudPanel endpoint="/departments" title="phòng ban"
            columns={[
              { title: 'Mã', dataIndex: 'maPhongBan', width: 100 },
              { title: 'Tên phòng ban', dataIndex: 'tenPhongBan' },
              { title: 'Mô tả', dataIndex: 'moTa' },
            ]}
            formFields={<>
              <Form.Item name="maPhongBan" label="Mã phòng ban" rules={[{ required: true }]}><Input /></Form.Item>
              <Form.Item name="tenPhongBan" label="Tên phòng ban" rules={[{ required: true }]}><Input /></Form.Item>
              <Form.Item name="moTa" label="Mô tả"><Input.TextArea rows={2} /></Form.Item>
            </>} />,
        },
        {
          key: 'pos', label: 'Chức vụ',
          children: <CrudPanel endpoint="/positions" title="chức vụ"
            columns={[
              { title: 'Tên chức vụ', dataIndex: 'tenChucVu' },
              { title: 'Lương cơ bản', dataIndex: 'luongCoBan', render: (v) => Number(v).toLocaleString('vi-VN') + ' đ' },
              { title: 'Phụ cấp', dataIndex: 'phuCapChucVu', render: (v) => Number(v).toLocaleString('vi-VN') + ' đ' },
            ]}
            formFields={<>
              <Form.Item name="tenChucVu" label="Tên chức vụ" rules={[{ required: true }]}><Input /></Form.Item>
              <Form.Item name="luongCoBan" label="Lương cơ bản" rules={[{ required: true }]}>
                <InputNumber style={{ width: '100%' }} min={0} step={1000000}
                  formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} /></Form.Item>
              <Form.Item name="phuCapChucVu" label="Phụ cấp">
                <InputNumber style={{ width: '100%' }} min={0} step={500000}
                  formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} /></Form.Item>
            </>} />,
        },
      ]} />
    </div>
  );
}
