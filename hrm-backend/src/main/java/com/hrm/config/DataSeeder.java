package com.hrm.config;

import com.hrm.entity.*;
import com.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PhongBanRepository phongBanRepo;
    private final ChucVuRepository chucVuRepo;
    private final NhanVienRepository nhanVienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final KhenThuongRepository khenThuongRepo;
    private final BoNhiemRepository boNhiemRepo;
    private final ThoiViecRepository thoiViecRepo;
    private final CaLamViecRepository caLamViecRepo;
    private final LichLamViecRepository lichLamViecRepo;
    private final TinTuyenDungRepository tinTuyenDungRepo;
    private final UngVienRepository ungVienRepo;
    private final ThongBaoRepository thongBaoRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (taiKhoanRepo.count() > 0) return; // da seed roi thi bo qua

        // ===== Phong ban =====
        PhongBan pbHR = phongBanRepo.save(PhongBan.builder().maPhongBan("HR").tenPhongBan("Phong Nhan su").moTa("Quan ly nhan su").build());
        PhongBan pbIT = phongBanRepo.save(PhongBan.builder().maPhongBan("IT").tenPhongBan("Phong Ky thuat").moTa("Phat trien phan mem").build());
        PhongBan pbKT = phongBanRepo.save(PhongBan.builder().maPhongBan("ACC").tenPhongBan("Phong Ke toan").moTa("Tai chinh ke toan").build());
        PhongBan pbMKT = phongBanRepo.save(PhongBan.builder().maPhongBan("MKT").tenPhongBan("Phong Marketing").moTa("Tiep thi truyen thong").build());

        // ===== Chuc vu =====
        ChucVu cvGiamDoc = chucVuRepo.save(ChucVu.builder().tenChucVu("Giam doc").luongCoBan(new BigDecimal("40000000")).phuCapChucVu(new BigDecimal("10000000")).build());
        ChucVu cvTruongPhong = chucVuRepo.save(ChucVu.builder().tenChucVu("Truong phong").luongCoBan(new BigDecimal("25000000")).phuCapChucVu(new BigDecimal("5000000")).build());
        ChucVu cvNhanVien = chucVuRepo.save(ChucVu.builder().tenChucVu("Nhan vien").luongCoBan(new BigDecimal("15000000")).phuCapChucVu(new BigDecimal("1000000")).build());

        // ===== Nhan vien (10 nguoi) =====
        String[][] nvData = {
            {"Quan Tri Vien",   "admin@company.com",    "0900000001", "Nam"},
            {"Truong Phong IT", "manager@company.com",  "0900000002", "Nam"},
            {"Nguyen Van A",    "nva@company.com",       "0900000003", "Nam"},
            {"Tran Thi Bich",   "ttb@company.com",       "0900000004", "Nu"},
            {"Le Van Cuong",    "lvc@company.com",       "0900000005", "Nam"},
            {"Pham Thi Dung",   "ptd@company.com",       "0900000006", "Nu"},
            {"Hoang Minh Duc",  "hmd@company.com",       "0900000007", "Nam"},
            {"Vu Thi Hanh",     "vth@company.com",       "0900000008", "Nu"},
            {"Dang Van Khoa",   "dvk@company.com",       "0900000009", "Nam"},
            {"Bui Thi Lan",     "btl@company.com",       "0900000010", "Nu"},
        };
        PhongBan[] pbs = {pbHR, pbIT, pbIT, pbKT, pbIT, pbMKT, pbIT, pbKT, pbMKT, pbHR};
        ChucVu[] cvs = {cvGiamDoc, cvTruongPhong, cvNhanVien, cvNhanVien, cvNhanVien, cvNhanVien, cvNhanVien, cvNhanVien, cvNhanVien, cvNhanVien};

        List<NhanVien> dsNV = new ArrayList<>();
        for (int i = 0; i < nvData.length; i++) {
            NhanVien nv = nhanVienRepo.save(NhanVien.builder()
                    .hoTen(nvData[i][0]).email(nvData[i][1]).sdt(nvData[i][2]).gioiTinh(nvData[i][3])
                    .cccd(String.format("%012d", i + 1))
                    .ngayVaoLam(LocalDate.of(2024, 1, 1).plusMonths(i))
                    .ngaySinh(LocalDate.of(1990, 1, 1).plusDays(i * 200L))
                    .diaChi("Ha Noi").phongBan(pbs[i]).chucVu(cvs[i]).build());
            dsNV.add(nv);
        }

        // ===== Tai khoan: USERNAME = EMAIL, mat khau 123456 =====
        TaiKhoan.VaiTro[] vaiTros = {
            TaiKhoan.VaiTro.ROLE_ADMIN, TaiKhoan.VaiTro.ROLE_MANAGER,
            TaiKhoan.VaiTro.ROLE_EMPLOYEE, TaiKhoan.VaiTro.ROLE_EMPLOYEE, TaiKhoan.VaiTro.ROLE_EMPLOYEE,
            TaiKhoan.VaiTro.ROLE_EMPLOYEE, TaiKhoan.VaiTro.ROLE_EMPLOYEE, TaiKhoan.VaiTro.ROLE_EMPLOYEE,
            TaiKhoan.VaiTro.ROLE_EMPLOYEE, TaiKhoan.VaiTro.ROLE_EMPLOYEE,
        };
        for (int i = 0; i < dsNV.size(); i++) {
            NhanVien nv = dsNV.get(i);
            taiKhoanRepo.save(TaiKhoan.builder()
                    .username(nv.getEmail())
                    .passwordHash(passwordEncoder.encode("123456"))
                    .vaiTro(vaiTros[i]).nhanVien(nv).active(true).build());
        }

        seedCaLamViec();
        seedKhenThuong(dsNV);
        seedBoNhiem(dsNV, pbHR, pbIT);
        seedThoiViec(dsNV);
        seedLichLamViec(dsNV);
        seedTuyenDung();
        seedThongBao();

        System.out.println(">>> Da seed du lieu mau. Dang nhap bang EMAIL (vd admin@company.com) - mat khau: 123456");
    }

    private void seedCaLamViec() {
        caLamViecRepo.save(CaLamViec.builder().tenCa("Ca Hanh Chinh").gioBatDau("08:00").gioKetThuc("17:30").moTa("Gio hanh chinh").build());
        caLamViecRepo.save(CaLamViec.builder().tenCa("Ca Sang").gioBatDau("06:00").gioKetThuc("14:00").moTa("Ca sang").build());
        caLamViecRepo.save(CaLamViec.builder().tenCa("Ca Chieu").gioBatDau("14:00").gioKetThuc("22:00").moTa("Ca chieu").build());
        caLamViecRepo.save(CaLamViec.builder().tenCa("Ca Dem").gioBatDau("22:00").gioKetThuc("06:00").moTa("Ca dem").build());
    }

    private void seedKhenThuong(List<NhanVien> ds) {
        String[] lyDo = {"Hoan thanh xuat sac KPI", "Sang kien cai tien", "Nhan vien xuat sac thang", "Dat giai thi dua", "Ho tro dong nghiep"};
        for (int i = 0; i < 10; i++) {
            khenThuongRepo.save(KhenThuong.builder()
                    .nhanVien(ds.get(i % ds.size()))
                    .lyDo(lyDo[i % lyDo.length])
                    .hinhThuc(i % 2 == 0 ? "Tien mat" : "Giay khen")
                    .soTien(new BigDecimal((i % 5 + 1) * 1000000))
                    .ngayKhenThuong(LocalDate.of(2026, 1, 1).plusDays(i * 15L))
                    .build());
        }
    }

    private void seedBoNhiem(List<NhanVien> ds, PhongBan tu, PhongBan toi) {
        for (int i = 0; i < 10; i++) {
            boNhiemRepo.save(BoNhiem.builder()
                    .nhanVien(ds.get(i % ds.size()))
                    .tuChucVu("Nhan vien").toiChucVu(i % 2 == 0 ? "Truong nhom" : "Pho phong")
                    .tuPhongBan(tu.getTenPhongBan()).toiPhongBan(toi.getTenPhongBan())
                    .ngayBoNhiem(LocalDate.of(2026, 2, 1).plusDays(i * 10L))
                    .lyDo("Nang cao nang luc quan ly").build());
        }
    }

    private void seedThoiViec(List<NhanVien> ds) {
        String[] lyDo = {"Chuyen cong tac", "Ly do ca nhan", "Het han hop dong", "Nghi huu", "Tim co hoi moi"};
        for (int i = 0; i < 10; i++) {
            thoiViecRepo.save(ThoiViec.builder()
                    .nhanVien(ds.get(i % ds.size()))
                    .ngayThoiViec(LocalDate.of(2026, 3, 1).plusDays(i * 12L))
                    .lyDo(lyDo[i % lyDo.length])
                    .trangThai(i % 3 == 0 ? "ChoDuyet" : "DaDuyet")
                    .build());
        }
    }

    private void seedLichLamViec(List<NhanVien> ds) {
        for (int i = 0; i < 10; i++) {
            lichLamViecRepo.save(LichLamViec.builder()
                    .nhanVien(ds.get(i % ds.size()))
                    .ngayLamViec(LocalDate.of(2026, 7, 1).plusDays(i))
                    .caLamViec("Ca Hanh Chinh")
                    .kieuNgay(i % 7 == 0 ? "Ngay le" : "Ngay thuong")
                    .build());
        }
    }

    private void seedTuyenDung() {
        String[] titles = {"Tuyen Lap trinh vien Java", "Tuyen Lap trinh vien ReactJS", "Tuyen Ke toan tong hop",
                "Tuyen Nhan vien Marketing", "Tuyen Chuyen vien Nhan su"};
        List<TinTuyenDung> tins = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            tins.add(tinTuyenDungRepo.save(TinTuyenDung.builder()
                    .maTin("MT" + String.format("%04d", i + 1))
                    .tieuDe(titles[i]).diaDiem(i % 2 == 0 ? "Ha Noi" : "HCM")
                    .ngayDang(LocalDate.of(2026, 6, 1).plusDays(i * 5L))
                    .hanNop(LocalDate.of(2026, 8, 1).plusDays(i * 5L))
                    .hieuLuc(true).moTa("Yeu cau kinh nghiem 1-2 nam").build()));
        }
        String[] tenUV = {"Dao Duy Tan", "Mai Tri Thuc", "Ngo Bao Chau", "Ly Thuong Kiet", "Tran Hung Dao",
                "Le Loi", "Nguyen Trai", "Pham Ngu Lao", "Dinh Bo Linh", "Ngo Quyen"};
        String[] trangThai = {"Moi", "DangXetDuyet", "DaPhongVan", "DatYeuCau", "TuChoi"};
        for (int i = 0; i < 10; i++) {
            ungVienRepo.save(UngVien.builder()
                    .hoTen(tenUV[i]).email("uv" + (i + 1) + "@gmail.com").sdt("09" + String.format("%08d", i + 1))
                    .viTriUngTuyen(tins.get(i % tins.size()).getTieuDe())
                    .maTin(tins.get(i % tins.size()).getMaTin())
                    .trangThai(trangThai[i % trangThai.length])
                    .diemDanhGia(i % 10 + 1)
                    .ngayNop(LocalDate.of(2026, 6, 10).plusDays(i * 3L)).build());
        }
    }

    private void seedThongBao() {
        String[] noiDung = {
            "Nhan vien moi vua duoc them vao he thong",
            "Co 3 don xin nghi phep dang cho duyet",
            "Bang luong thang nay da duoc chot",
            "Hop dong cua Nguyen Van A sap het han",
            "Lich hop toan cong ty luc 9h sang mai",
        };
        for (int i = 0; i < noiDung.length; i++) {
            thongBaoRepo.save(ThongBao.builder()
                    .noiDung(noiDung[i]).loai(i % 2 == 0 ? "info" : "warning")
                    .daDoc(false).build());
        }
    }
}
