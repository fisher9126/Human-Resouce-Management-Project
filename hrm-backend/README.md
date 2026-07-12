# HRM Backend — Hệ thống Quản lý Nhân sự

Backend Spring Boot 3 + MySQL cho hệ thống HRM (theo tài liệu SRS v1.1).

## Công nghệ
- Java 17, Spring Boot 3.3.2
- Spring Web, Spring Data JPA, Spring Security + JWT (jjwt 0.12)
- Lombok, Bean Validation
- MySQL 8

## Yêu cầu môi trường
- JDK 17+
- Maven 3.9+
- MySQL đang chạy ở localhost:3306

## Cấu hình trước khi chạy
Mở `src/main/resources/application.properties` và sửa:
- `spring.datasource.password` = mật khẩu MySQL của bạn.
- `app.jwt.secret` = đổi thành chuỗi Base64 riêng (≥ 32 ký tự) cho an toàn.

Database `hrm_db` sẽ **tự tạo** khi chạy lần đầu (nhờ `createDatabaseIfNotExist=true`), và các bảng cũng tự sinh bằng Hibernate (`ddl-auto=update`).

## Chạy
```bash
mvn spring-boot:run
```
Server chạy ở `http://localhost:8080`.

Lần đầu chạy, DataSeeder tự tạo 3 tài khoản mẫu (mật khẩu đều là `123456`):
| Username | Vai trò | Quyền |
|----------|---------|-------|
| admin | ROLE_ADMIN | Toàn quyền |
| manager | ROLE_MANAGER | Duyệt đơn, xem báo cáo |
| nhanvien | ROLE_EMPLOYEE | Chấm công, gửi đơn, xem lương |

## Đăng nhập lấy token
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```
Lấy `accessToken` trong response, các request sau thêm header:
`Authorization: Bearer <accessToken>`

## Danh sách API

### Auth
- `POST /api/v1/auth/login` — đăng nhập

### Nhân viên (FR-1)
- `GET /api/v1/employees?keyword=&phongBanId=&chucVuId=&page=0&size=10` — tìm kiếm/lọc/phân trang
- `GET /api/v1/employees/{id}` — chi tiết
- `POST /api/v1/employees` — thêm (ADMIN)
- `PUT /api/v1/employees/{id}` — sửa (ADMIN)
- `DELETE /api/v1/employees/{id}` — vô hiệu hóa (ADMIN)

### Phòng ban / Chức vụ (FR-2)
- `GET|POST|PUT|DELETE /api/v1/departments`
- `GET|POST|PUT|DELETE /api/v1/positions`

### Chấm công (FR-3)
- `POST /api/v1/attendance/check-in`
- `POST /api/v1/attendance/check-out`
- `GET /api/v1/attendance/me?thang=&nam=`
- `GET /api/v1/attendance/{nhanVienId}?thang=&nam=`

### Nghỉ phép (FR-4)
- `POST /api/v1/leaves/request` — gửi đơn
- `GET /api/v1/leaves/me` — đơn của tôi
- `GET /api/v1/leaves/pending` — đơn chờ duyệt (ADMIN/MANAGER)
- `PUT /api/v1/leaves/approve/{id}` — duyệt/từ chối (ADMIN/MANAGER)

### Tính lương (FR-6)
- `POST /api/v1/payroll/calculate` — tính lương toàn công ty (ADMIN/MANAGER)
- `PUT /api/v1/payroll/{id}/approve` — chốt bảng lương (ADMIN)
- `GET /api/v1/payroll?thang=&nam=` — bảng lương tháng
- `GET /api/v1/payroll/me` — phiếu lương của tôi

### Hợp đồng (FR-5)
- `POST /api/v1/contracts/employee/{nhanVienId}` — thêm hợp đồng (ADMIN)
- `GET /api/v1/contracts/employee/{nhanVienId}` — HĐ của 1 NV
- `GET /api/v1/contracts` — tất cả
- `GET /api/v1/contracts/expiring` — HĐ sắp hết hạn (30 ngày)

### Báo cáo (FR-7)
- `GET /api/v1/reports/dashboard` — tổng quan
- `GET /api/v1/reports/employees-by-department` — cơ cấu NV theo phòng ban
- `GET /api/v1/reports/payroll-summary?thang=&nam=` — tổng quỹ lương

## Quy tắc nghiệp vụ đã cài
- **Chấm công:** đi trễ nếu check-in sau 08:15 (grace 15'); về sớm nếu check-out trước 17:30; trừ nghỉ trưa 90'.
- **Nghỉ phép:** kiểm tra quỹ phép năm (mặc định 12 ngày/NV), chặn trùng lịch với đơn đã duyệt, tự trừ phép khi duyệt.
- **Lương:** bảo hiểm 10.5% (BHXH 8% + BHYT 1.5% + BHTN 1%); thuế TNCN lũy tiến 7 bậc; giảm trừ bản thân 11tr + 4.4tr/người phụ thuộc.

## Ghi chú
- Bảo mật: BCrypt strength 10, JWT hết hạn 12h, RBAC theo vai trò.
- Phần frontend làm sau — backend đã bật CORS cho `localhost:3000` và `localhost:5173`.
