import { Form, Select, DatePicker } from 'antd';
import dayjs from 'dayjs';
import CrudListPage from '../components/CrudListPage';
import useEmployees from '../context/useEmployees';

export default function Schedules() {
  const employees = useEmployees();

  const columns = [
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'], render: (t) => t || '—' },
    { title: 'Ngày làm việc', dataIndex: 'ngayLamViec', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : '—' },
    { title: 'Ca làm việc', dataIndex: 'caLamViec' },
    { title: 'Kiểu ngày', dataIndex: 'kieuNgay', render: (t) => t },
  ];

  const formFields = (
    <>
      <Form.Item name="nhanVienId" label="Nhân viên" rules={[{ required: true }]}>
        <Select showSearch optionFilterProp="label" options={employees} />
      </Form.Item>
      <Form.Item name="ngayLamViec" label="Ngày làm việc" rules={[{ required: true }]}>
        <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
      </Form.Item>
      <Form.Item name="caLamViec" label="Ca làm việc" rules={[{ required: true }]}>
        <Select options={[
          { value: 'Ca Hành chính', label: 'Ca Hành chính' },
          { value: 'Ca Sáng', label: 'Ca Sáng' },
          { value: 'Ca Chiều', label: 'Ca Chiều' },
          { value: 'Ca Đêm', label: 'Ca Đêm' },
        ]} />
      </Form.Item>
      <Form.Item name="kieuNgay" label="Kiểu ngày" initialValue="Ngày thường">
        <Select options={[{ value: 'Ngày thường', label: 'Ngày thường' }, { value: 'Ngày lễ', label: 'Ngày lễ' }]} />
      </Form.Item>
    </>
  );

  const buildPayload = (v) => ({ ...v, ngayLamViec: v.ngayLamViec?.format('YYYY-MM-DD') });

  return <CrudListPage title="Lịch làm việc" endpoint="/schedules"
    columns={columns} formFields={formFields} buildPayload={buildPayload} />;
}
