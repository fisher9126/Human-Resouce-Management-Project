package com.hrm.config;

import com.hrm.entity.*;
import com.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PhongBanRepository phongBanRepo;
    private final ChucVuRepository chucVuRepo;
    private final NhanVienRepository nhanVienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (taiKhoanRepo.count() > 0) return; // da seed roi thi bo qua

        // Phong ban
        PhongBan pbHR = phongBanRepo.save(PhongBan.builder()
                .maPhongBan("HR").tenPhongBan("Phong Nhan su").moTa("Quan ly nhan su").build());
        PhongBan pbIT = phongBanRepo.save(PhongBan.builder()
                .maPhongBan("IT").tenPhongBan("Phong Ky thuat").moTa("Phat trien phan mem").build());

        // Chuc vu
        ChucVu cvGiamDoc = chucVuRepo.save(ChucVu.builder()
                .tenChucVu("Giam doc").luongCoBan(new BigDecimal("40000000"))
                .phuCapChucVu(new BigDecimal("10000000")).build());
        ChucVu cvTruongPhong = chucVuRepo.save(ChucVu.builder()
                .tenChucVu("Truong phong").luongCoBan(new BigDecimal("25000000"))
                .phuCapChucVu(new BigDecimal("5000000")).build());
        ChucVu cvNhanVien = chucVuRepo.save(ChucVu.builder()
                .tenChucVu("Nhan vien").luongCoBan(new BigDecimal("15000000"))
                .phuCapChucVu(new BigDecimal("1000000")).build());

        // Nhan vien Admin
        NhanVien admin = nhanVienRepo.save(NhanVien.builder()
                .hoTen("Quan tri vien").cccd("000000000001").sdt("0900000001")
                .email("admin@company.com").ngayVaoLam(LocalDate.of(2024, 1, 1))
                .gioiTinh("Nam").phongBan(pbHR).chucVu(cvGiamDoc).build());

        NhanVien manager = nhanVienRepo.save(NhanVien.builder()
                .hoTen("Truong phong IT").cccd("000000000002").sdt("0900000002")
                .email("manager@company.com").ngayVaoLam(LocalDate.of(2024, 2, 1))
                .gioiTinh("Nam").phongBan(pbIT).chucVu(cvTruongPhong).build());

        NhanVien nv = nhanVienRepo.save(NhanVien.builder()
                .hoTen("Nguyen Van A").cccd("000000000003").sdt("0900000003")
                .email("nva@company.com").ngayVaoLam(LocalDate.of(2024, 3, 1))
                .gioiTinh("Nam").phongBan(pbIT).chucVu(cvNhanVien).build());

        // Tai khoan (mat khau: 123456)
        taiKhoanRepo.save(TaiKhoan.builder()
                .username("admin").passwordHash(passwordEncoder.encode("123456"))
                .vaiTro(TaiKhoan.VaiTro.ROLE_ADMIN).nhanVien(admin).active(true).build());
        taiKhoanRepo.save(TaiKhoan.builder()
                .username("manager").passwordHash(passwordEncoder.encode("123456"))
                .vaiTro(TaiKhoan.VaiTro.ROLE_MANAGER).nhanVien(manager).active(true).build());
        taiKhoanRepo.save(TaiKhoan.builder()
                .username("nhanvien").passwordHash(passwordEncoder.encode("123456"))
                .vaiTro(TaiKhoan.VaiTro.ROLE_EMPLOYEE).nhanVien(nv).active(true).build());

        System.out.println(">>> Da seed du lieu mau. Login: admin/manager/nhanvien - mat khau: 123456");
    }
}
