# HRM Frontend — Giao diện Hệ thống Quản lý Nhân sự

Frontend React + Vite + Ant Design cho hệ thống HRM. Kết nối với backend Spring Boot (`hrm-backend`).

## Công nghệ
- React 18 + Vite 5
- Ant Design 5 (UI) + @ant-design/plots (biểu đồ)
- React Router 6 (điều hướng + phân quyền route)
- Axios (gọi API, tự gắn JWT)
- Framer Motion (animation) + React CountUp (đếm số liệu)

## Yêu cầu
- Node.js 18+
- Backend `hrm-backend` đang chạy ở `http://localhost:8080`

## Cài & chạy
```bash
cd hrm-frontend
npm install
npm run dev
```
Mở `http://localhost:5173`.

> Vite đã cấu hình **proxy** `/api` → `http://localhost:8080`, nên không lo CORS khi dev.

## Đăng nhập demo
| Username | Mật khẩu | Vai trò |
|----------|----------|---------|
| admin | 123456 | Quản trị viên (thấy tất cả) |
| manager | 123456 | Trưởng phòng |
| nhanvien | 123456 | Nhân viên |

## Các màn hình
| Trang | Đường dẫn | Vai trò |
|-------|-----------|---------|
| Đăng nhập | /login | Tất cả |
| Tổng quan (Dashboard) | / | Admin/Manager có thống kê + chart; NV có trang chào |
| Nhân viên | /employees | Admin (CRUD), Manager (xem) |
| Phòng ban & Chức vụ | /org | Admin |
| Chấm công | /attendance | Tất cả (check-in/out + lịch sử) |
| Nghỉ phép | /leaves | Tất cả (gửi đơn); Admin/Manager (duyệt) |
| Tính lương | /payroll | Admin/Manager (tính+chốt); NV (xem phiếu) |
| Hợp đồng | /contracts | Admin |
| Báo cáo | /reports | Admin/Manager |

## Điểm nhấn giao diện
- **Login:** nền gradient động, card + logo có animation spring.
- **Sidebar:** gradient tím-chàm, menu tự ẩn/hiện theo vai trò.
- **Dashboard:** 4 thẻ số liệu gradient, số **đếm tăng dần** (CountUp), hover nổi lên; biểu đồ cột động.
- **Chuyển trang:** hiệu ứng fade + trượt (Framer Motion).
- **Báo cáo:** biểu đồ cột + tròn (donut) có animation.
- **Chấm công:** đồng hồ thời gian thực, nút check-in/out.

## Ghi chú
- Token JWT lưu ở localStorage, tự gắn header + tự logout khi hết hạn (401).
- Frontend chưa có upload ảnh file thật (field ảnh nhận URL text) — khớp với backend hiện tại.
