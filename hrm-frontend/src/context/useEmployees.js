import { useEffect, useState } from 'react';
import api from '../api/client';

// Lay danh sach nhan vien (dung cho dropdown chon NV trong cac form)
export default function useEmployees() {
  const [employees, setEmployees] = useState([]);
  useEffect(() => {
    api.get('/employees', { params: { page: 0, size: 200 } })
      .then((r) => setEmployees(r.data.data.content))
      .catch(() => {});
  }, []);
  return employees.map((e) => ({ value: e.id, label: `${e.hoTen} (${e.id})` }));
}
