import { Form, Input, TimePicker } from 'antd';
import CrudListPage from '../components/CrudListPage';

export default function Shifts() {
  const columns = [
    { title: 'Tên ca', dataIndex: 'tenCa' },
    { title: 'Giờ bắt đầu', dataIndex: 'gioBatDau' },
    { title: 'Giờ kết thúc', dataIndex: 'gioKetThuc' },
    { title: 'Mô tả', dataIndex: 'moTa', ellipsis: true },
  ];

  const formFields = (
    <>
      <Form.Item name="tenCa" label="Tên ca" rules={[{ required: true }]}><Input placeholder="VD: Ca Hành chính" /></Form.Item>
      <Form.Item name="gioBatDau" label="Giờ bắt đầu (HH:mm)" rules={[{ required: true }]}><Input placeholder="08:00" /></Form.Item>
      <Form.Item name="gioKetThuc" label="Giờ kết thúc (HH:mm)" rules={[{ required: true }]}><Input placeholder="17:30" /></Form.Item>
      <Form.Item name="moTa" label="Mô tả"><Input /></Form.Item>
    </>
  );

  return <CrudListPage title="Ca làm việc" endpoint="/shifts" columns={columns} formFields={formFields} />;
}
