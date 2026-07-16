import { Form, Select, Input, InputNumber, DatePicker } from 'antd';
import dayjs from 'dayjs';
import CrudListPage from '../components/CrudListPage';
import useEmployees from '../context/useEmployees';

const money = (v) => v != null ? Number(v).toLocaleString('vi-VN') + ' đ' : '—';

export default function Rewards() {
  const employees = useEmployees();

  const columns = [
    { title: 'Nhân viên', dataIndex: ['nhanVien', 'hoTen'], render: (t) => t || '—' },
    { title: 'Lý do', dataIndex: 'lyDo', ellipsis: true },
    { title: 'Hình thức', dataIndex: 'hinhThuc' },
    { title: 'Số tiền', dataIndex: 'soTien', render: money },
    { title: 'Ngày', dataIndex: 'ngayKhenThuong', render: (t) => t ? dayjs(t).format('DD/MM/YYYY') : '—' },
  ];

  const formFields = (
    <>
      <Form.Item name="nhanVienId" label="Nhân viên" rules={[{ required: true }]}>
        <Select showSearch optionFilterProp="label" options={employees} />
      </Form.Item>
      <Form.Item name="lyDo" label="Lý do khen thưởng" rules={[{ required: true }]}>
        <Input.TextArea rows={2} />
      </Form.Item>
      <Form.Item name="hinhThuc" label="Hình thức" rules={[{ required: true }]}>
        <Select options={[{ value: 'Tiền mặt', label: 'Tiền mặt' }, { value: 'Giấy khen', label: 'Giấy khen' }]} />
      </Form.Item>
      <Form.Item name="soTien" label="Số tiền">
        <InputNumber style={{ width: '100%' }} min={0} step={500000}
          formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} />
      </Form.Item>
      <Form.Item name="ngayKhenThuong" label="Ngày khen thưởng" rules={[{ required: true }]}>
        <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
      </Form.Item>
    </>
  );

  const buildPayload = (v) => ({
    nhanVienId: v.nhanVienId, lyDo: v.lyDo, hinhThuc: v.hinhThuc,
    soTien: v.soTien, ngayKhenThuong: v.ngayKhenThuong?.format('YYYY-MM-DD'),
  });

  return <CrudListPage title="Quản lý Khen thưởng" endpoint="/rewards"
    columns={columns} formFields={formFields} buildPayload={buildPayload} />;
}
