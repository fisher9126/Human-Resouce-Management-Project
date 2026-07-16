import { useEffect, useState } from 'react';
import { Badge, Popover, List, Button, Tag, Empty } from 'antd';
import { BellOutlined, CheckOutlined } from '@ant-design/icons';
import api from '../api/client';

export default function NotificationBell() {
  const [items, setItems] = useState([]);
  const [unread, setUnread] = useState(0);
  const [open, setOpen] = useState(false);

  const load = async () => {
    try {
      const r = await api.get('/notifications');
      setItems(r.data.data.danhSach || []);
      setUnread(r.data.data.chuaDoc || 0);
    } catch { /* ignore */ }
  };
  useEffect(() => {
    load();
    const t = setInterval(load, 60 * 1000); // refresh moi phut
    return () => clearInterval(t);
  }, []);

  const readAll = async () => {
    try { await api.put('/notifications/read-all'); load(); } catch { /* ignore */ }
  };
  const readOne = async (id) => {
    try { await api.put(`/notifications/${id}/read`); load(); } catch { /* ignore */ }
  };

  const content = (
    <div style={{ width: 320, maxHeight: 380, overflowY: 'auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
        <b>Thông báo</b>
        {unread > 0 && <Button size="small" type="link" icon={<CheckOutlined />} onClick={readAll}>Đọc tất cả</Button>}
      </div>
      {items.length ? (
        <List
          dataSource={items}
          renderItem={(n) => (
            <List.Item
              style={{ cursor: 'pointer', opacity: n.daDoc ? 0.55 : 1, padding: '8px 4px' }}
              onClick={() => !n.daDoc && readOne(n.id)}>
              <List.Item.Meta
                title={<span style={{ fontSize: 13 }}>{n.noiDung}</span>}
                description={<Tag color={n.loai === 'warning' ? 'orange' : 'blue'} style={{ fontSize: 10 }}>
                  {n.loai === 'warning' ? 'Cảnh báo' : 'Thông tin'}</Tag>}
              />
            </List.Item>
          )}
        />
      ) : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Không có thông báo" />}
    </div>
  );

  return (
    <Popover content={content} trigger="click" open={open} onOpenChange={setOpen} placement="bottomRight">
      <Badge count={unread} size="small" offset={[-2, 2]}>
        <Button type="text" shape="circle" icon={<BellOutlined style={{ fontSize: 18 }} />} />
      </Badge>
    </Popover>
  );
}
