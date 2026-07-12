import { useState } from 'react';
import { Card, Form, Input, Button, Typography, App } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';

const { Title, Text } = Typography;

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      await login(values.username, values.password);
      message.success('Đăng nhập thành công!');
      navigate('/');
    } catch (e) {
      message.error(e.response?.data?.message || 'Sai tên đăng nhập hoặc mật khẩu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-bg">
      <motion.div
        initial={{ opacity: 0, y: 30, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}>
        <Card style={{ width: 400, borderRadius: 20, boxShadow: '0 20px 60px rgba(0,0,0,0.25)' }} bordered={false}>
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <motion.div
              initial={{ scale: 0 }} animate={{ scale: 1 }}
              transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
              style={{ fontSize: 56 }}>👥</motion.div>
            <Title level={3} style={{ marginBottom: 0, marginTop: 8 }}>HRM System</Title>
            <Text type="secondary">Hệ thống Quản lý Nhân sự</Text>
          </div>

          <Form onFinish={onFinish} size="large" layout="vertical">
            <Form.Item name="username" rules={[{ required: true, message: 'Nhập tên đăng nhập' }]}>
              <Input prefix={<UserOutlined />} placeholder="Tên đăng nhập" />
            </Form.Item>
            <Form.Item name="password" rules={[{ required: true, message: 'Nhập mật khẩu' }]}>
              <Input.Password prefix={<LockOutlined />} placeholder="Mật khẩu" />
            </Form.Item>
            <Form.Item style={{ marginBottom: 8 }}>
              <Button type="primary" htmlType="submit" block loading={loading}
                style={{ height: 46, fontWeight: 600,
                  background: 'linear-gradient(135deg,#4f46e5,#7c3aed)', border: 'none' }}>
                Đăng nhập
              </Button>
            </Form.Item>
          </Form>

          <div style={{ textAlign: 'center', marginTop: 8 }}>
            <Text type="secondary" style={{ fontSize: 12 }}>
              Demo: admin / manager / nhanvien — mật khẩu <b>123456</b>
            </Text>
          </div>
        </Card>
      </motion.div>
    </div>
  );
}
