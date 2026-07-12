import { useEffect, useState } from 'react';
import {
  Card, Row, Col, Avatar, Descriptions, Button, Modal, Form, Input,
  Upload, App, Typography, Space, Tag, Spin,
} from 'antd';
import { UserOutlined, LockOutlined, UploadOutlined, EditOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;

const TRANG_THAI = {
  DangLamViec: { text: 'Đang làm việc', color: 'green' },
  NghiThaiSan: { text: 'Nghỉ thai sản', color: 'orange' },
  DaNghiViec: { text: 'Đã nghỉ việc', color: 'red' },
};

export default function Profile() {
  const { user, updateUser } = useAuth();
  const { message } = App.useApp();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pwOpen, setPwOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [pwForm] = Form.useForm();
  const [editForm] = Form.useForm();

  const load = async () => {
    setLoading(true);
    try { const r = await api.get('/profile'); setProfile(r.data.data); }
    catch { message.error('Không tải được thông tin'); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const doiMatKhau = async () => {
    const v = await pwForm.validateFields();
    try {
      await api.put('/profile/change-password', {
        matKhauCu: v.matKhauCu, matKhauMoi: v.matKhauMoi, xacNhanMatKhau: v.xacNhanMatKhau,
      });
      message.success('Đổi mật khẩu thành công');
      setPwOpen(false); pwForm.resetFields();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  const luuThongTin = async () => {
    const v = await editForm.validateFields();
    try {
      await api.put('/profile', { sdt: v.sdt, diaChi: v.diaChi });
      message.success('Cập nhật thành công');
      setEditOpen(false); load();
    } catch (e) { message.error(e.response?.data?.message || 'Có lỗi'); }
  };

  // Upload avatar qua backend -> Cloudinary -> luu URL
  const uploadAvatar = async (file) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) { message.error('Vui lòng chọn file ảnh'); return false; }
    if (file.size > 5 * 1024 * 1024) { message.error('Ảnh tối đa 5MB'); return false; }
    const formData = new FormData();
    formData.append('file', file);
    try {
      const res = await api.post('/profile/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      message.success('Đã cập nhật ảnh đại diện');
      // Cap nhat avatar trong context -> header doi ngay
      updateUser({ imageUrl: res.data.data.imageUrl });
      load();
    } catch (e) { message.error(e.response?.data?.message || 'Không cập nhật được ảnh'); }
    return false; // chan upload mac dinh cua antd
  };

  if (loading) return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>;

  const tt = TRANG_THAI[profile?.trangThai] || { text: profile?.trangThai, color: 'default' };

  return (
    <div>
      <Title level={3} style={{ marginBottom: 16 }}>Thông tin cá nhân</Title>
      <Row gutter={[20, 20]}>
        <Col xs={24} md={8}>
          <motion.div initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.4 }}>
            <Card style={{ borderRadius: 16, textAlign: 'center' }}>
              <Avatar size={110} src={profile?.anh || undefined} icon={<UserOutlined />}
                style={{ background: '#4f46e5', marginBottom: 12 }} />
              <Title level={4} style={{ margin: '8px 0 2px' }}>{profile?.hoTen}</Title>
              <Tag color={tt.color}>{tt.text}</Tag>
              <div style={{ marginTop: 16 }}>
                <Upload accept="image/*" showUploadList={false} beforeUpload={uploadAvatar}>
                  <Button icon={<UploadOutlined />} block>Đổi ảnh đại diện</Button>
                </Upload>
              </div>
            </Card>
          </motion.div>
        </Col>

        <Col xs={24} md={16}>
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
            <Card style={{ borderRadius: 16 }}
              title="Chi tiết hồ sơ"
              extra={
                <Space>
                  <Button icon={<EditOutlined />} onClick={() => {
                    editForm.setFieldsValue({ sdt: profile?.sdt, diaChi: profile?.diaChi });
                    setEditOpen(true);
                  }}>Sửa thông tin</Button>
                  <Button type="primary" icon={<LockOutlined />} onClick={() => setPwOpen(true)}>Đổi mật khẩu</Button>
                </Space>
              }>
              <Descriptions column={1} bordered size="middle">
                <Descriptions.Item label="Họ tên">{profile?.hoTen}</Descriptions.Item>
                <Descriptions.Item label="Email (tên đăng nhập)">{profile?.email}</Descriptions.Item>
                <Descriptions.Item label="Số điện thoại">{profile?.sdt || '—'}</Descriptions.Item>
                <Descriptions.Item label="CCCD">{profile?.cccd}</Descriptions.Item>
                <Descriptions.Item label="Giới tính">{profile?.gioiTinh || '—'}</Descriptions.Item>
                <Descriptions.Item label="Ngày sinh">
                  {profile?.ngaySinh ? dayjs(profile.ngaySinh).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
                <Descriptions.Item label="Địa chỉ">{profile?.diaChi || '—'}</Descriptions.Item>
                <Descriptions.Item label="Phòng ban">{profile?.phongBan?.tenPhongBan || '—'}</Descriptions.Item>
                <Descriptions.Item label="Chức vụ">{profile?.chucVu?.tenChucVu || '—'}</Descriptions.Item>
                <Descriptions.Item label="Ngày vào làm">
                  {profile?.ngayVaoLam ? dayjs(profile.ngayVaoLam).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
                <Descriptions.Item label="Số ngày phép còn lại">{profile?.soNgayPhepConLai ?? '—'}</Descriptions.Item>
              </Descriptions>
            </Card>
          </motion.div>
        </Col>
      </Row>

      {/* Modal doi mat khau */}
      <Modal title="Đổi mật khẩu" open={pwOpen} onOk={doiMatKhau} onCancel={() => setPwOpen(false)}
        okText="Xác nhận đổi" cancelText="Hủy" destroyOnClose>
        <Form form={pwForm} layout="vertical">
          <Form.Item name="matKhauCu" label="Mật khẩu hiện tại" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="matKhauMoi" label="Mật khẩu mới"
            rules={[{ required: true }, { min: 6, message: 'Tối thiểu 6 ký tự' }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="xacNhanMatKhau" label="Xác nhận mật khẩu mới" dependencies={['matKhauMoi']}
            rules={[
              { required: true },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('matKhauMoi') === value) return Promise.resolve();
                  return Promise.reject(new Error('Mật khẩu xác nhận không khớp'));
                },
              }),
            ]}>
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>

      {/* Modal sua thong tin */}
      <Modal title="Sửa thông tin cá nhân" open={editOpen} onOk={luuThongTin} onCancel={() => setEditOpen(false)}
        okText="Lưu" cancelText="Hủy" destroyOnClose>
        <Form form={editForm} layout="vertical">
          <Form.Item name="sdt" label="Số điện thoại"><Input /></Form.Item>
          <Form.Item name="diaChi" label="Địa chỉ"><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
