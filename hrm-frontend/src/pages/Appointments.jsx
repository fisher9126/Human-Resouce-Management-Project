import { Form, Select, Input, DatePicker } from 'antd';
import dayjs from 'dayjs';
import CrudListPage from '../components/CrudListPage';
import useEmployees from '../context/useEmployees';

export default function Appointments() {
  const employees = useEmployees();

  const columns = [
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'], render: (t) => t || '—' },
    { title: 'Từ chức vụ', dataIndex: 'tuChucVu' },
    { title: 'Tới chức vụ', dataIndex: 'toiChucVu' },
    { title: 'Từ phòng ban', dataIndex: 'tuPhongBan' },
    { title: 'Tới phòng ban', dataIndex: 'toiPhongBan' },
    { title: 'Ngày', dataIndex: 'ngayBoNhiem', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : '—' },
  ];

  const formFields = (
    <>
      <Form.Item name="nhanVienId" label="Nhân viên" rules={[{ required: true }]}>
        <Select showSearch optionFilterProp="label" options={employees} />
      </Form.Item>
      <Form.Item name="tuChucVu" label="Từ chức vụ"><Input /></Form.Item>
      <Form.Item name="toiChucVu" label="Tới chức vụ" rules={[{ required: true }]}><Input /></Form.Item>
      <Form.Item name="tuPhongBan" label="Từ phòng ban"><Input /></Form.Item>
      <Form.Item name="toiPhongBan" label="Tới phòng ban"><Input /></Form.Item>
      <Form.Item name="lyDo" label="Lý do"><Input.TextArea rows={2} /></Form.Item>
      <Form.Item name="ngayBoNhiem" label="Ngày bổ nhiệm" rules={[{ required: true }]}>
        <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
      </Form.Item>
    </>
  );

  const buildPayload = (v) => ({ ...v, ngayBoNhiem: v.ngayBoNhiem?.format('YYYY-MM-DD') });

  return <CrudListPage title="Quản lý Bổ nhiệm" endpoint="/appointments"
    columns={columns} formFields={formFields} buildPayload={buildPayload} />;
}
