import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, App, Typography, Space, Popconfirm } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import api from '../api/client';
import useScrollReveal from '../context/useScrollReveal';

const { Title } = Typography;

/**
 * Trang danh sach + them moi dung chung cho cac module don gian.
 * props:
 *  - title: tieu de trang
 *  - endpoint: API base (vd '/rewards')
 *  - columns: cot bang (antd)
 *  - formFields: JSX cac Form.Item trong modal them moi
 *  - buildPayload: (values) => object gui len API (mac dinh: chinh values)
 *  - canCreate: co hien nut them khong (default true)
 *  - canDelete: co cot xoa khong (default true)
 */
export default function CrudListPage({
  title, endpoint, columns, formFields, buildPayload,
  canCreate = true, canDelete = true,
}) {
  const { message } = App.useApp();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();
  useScrollReveal([loading]);

  const load = async () => {
    setLoading(true);
    try { const r = await api.get(endpoint); setData(r.data.data); }
    catch { message.error('Không tải được dữ liệu'); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, [endpoint]);

  const submit = async () => {
    const v = await form.validateFields();
    const payload = buildPayload ? buildPayload(v) : v;
    try {
      await api.post(endpoint, payload);
      message.success('Thêm thành công');
      setOpen(false); form.resetFields(); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const remove = async (id) => {
    try { await api.delete(`${endpoint}/${id}`); message.success('Đã xóa'); load(); }
    catch (e) { message.error(e.response?.data?.message || 'Không xóa được'); }
  };

  const cols = canDelete
    ? [...columns, {
        title: 'Thao tác', width: 90, render: (_, r) => (
          <Popconfirm title="Xóa mục này?" onConfirm={() => remove(r.id)}>
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        ),
      }]
    : columns;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={3} className="grad-title" style={{ margin: 0 }}>{title}</Title>
        {canCreate && <Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>Thêm mới</Button>}
      </div>
      <div className="reveal">
        <Card style={{ borderRadius: 16 }}>
          <Table rowKey="id" columns={cols} dataSource={data} loading={loading}
            scroll={{ x: 'max-content' }} pagination={{ pageSize: 8 }} />
        </Card>
      </div>
      {canCreate && (
        <Modal title={`Thêm ${title.toLowerCase()}`} open={open} onOk={submit} onCancel={() => setOpen(false)}
          okText="Lưu" cancelText="Hủy" width={560} destroyOnClose>
          <Form form={form} layout="vertical">{formFields}</Form>
        </Modal>
      )}
    </div>
  );
}
