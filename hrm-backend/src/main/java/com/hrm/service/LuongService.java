package com.hrm.service;

import com.hrm.dto.TinhLuongRequest;
import com.hrm.entity.ChucVu;
import com.hrm.entity.Luong;
import com.hrm.entity.NhanVien;
import com.hrm.repository.LuongRepository;
import com.hrm.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LuongService {

    private final LuongRepository luongRepo;
    private final NhanVienRepository nhanVienRepo;
    private final ChamCongService chamCongService;

    /** Tinh luong cho toan bo nhan vien dang lam viec trong thang. */
    @Transactional
    public List<Luong> tinhLuongToanCongTy(TinhLuongRequest req) {
        List<NhanVien> dsNV = nhanVienRepo.findAll().stream()
                .filter(nv -> nv.getTrangThai() != NhanVien.TrangThaiNhanVien.DaNghiViec)
                .toList();

        List<Luong> ketQua = new ArrayList<>();
        for (NhanVien nv : dsNV) {
            ketQua.add(tinhLuongMotNguoi(nv, req));
        }
        return ketQua;
    }

    @Transactional
    public Luong tinhLuongMotNguoi(NhanVien nv, TinhLuongRequest req) {
        ChucVu cv = nv.getChucVu();
        if (cv == null) throw new IllegalStateException("Nhan vien " + nv.getHoTen() + " chua co chuc vu");

        BigDecimal luongThoaThuan = cv.getLuongCoBan();
        BigDecimal phuCap = cv.getPhuCapChucVu() != null ? cv.getPhuCapChucVu() : BigDecimal.ZERO;

        // Ngay cong thuc te tu cham cong
        double ngayCongThucTe = chamCongService.tongNgayCong(nv.getId(), req.getThang(), req.getNam());
        int ngayChuan = (req.getNgayCongChuan() == null || req.getNgayCongChuan() <= 0) ? 26 : req.getNgayCongChuan();

        // Neu chua cham cong ngay nao -> tinh full cong (tranh luong = 0 khi demo)
        double heSoCong = ngayCongThucTe > 0 ? Math.min(1.0, ngayCongThucTe / ngayChuan) : 1.0;

        BigDecimal luongTheoCong = luongThoaThuan
                .multiply(BigDecimal.valueOf(heSoCong))
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal thuong = req.getThuong() != null ? req.getThuong() : BigDecimal.ZERO;

        // Tong thu nhap
        BigDecimal tongThuNhap = luongTheoCong.add(phuCap).add(thuong);

        // Bao hiem 10.5% tren luong thoa thuan (theo SRS dung luong dong BH)
        BigDecimal baoHiem = ThueTNCNCalculator.tinhBaoHiem(luongThoaThuan);

        // Thu nhap tinh thue = tong thu nhap - bao hiem - giam tru gia canh
        BigDecimal giamTru = ThueTNCNCalculator.giamTruGiaCanh(
                req.getSoNguoiPhuThuoc() != null ? req.getSoNguoiPhuThuoc() : 0);
        BigDecimal thuNhapTinhThue = tongThuNhap.subtract(baoHiem).subtract(giamTru);
        BigDecimal thue = ThueTNCNCalculator.tinhThue(thuNhapTinhThue);

        // Thuc nhan = tong thu nhap - bao hiem - thue
        BigDecimal thucNhan = tongThuNhap.subtract(baoHiem).subtract(thue);

        // Luu (upsert theo nhanVien + thang + nam)
        Luong luong = luongRepo.findByNhanVienIdAndThangAndNam(nv.getId(), req.getThang(), req.getNam())
                .orElse(Luong.builder().nhanVien(nv).thang(req.getThang()).nam(req.getNam()).build());

        luong.setLuongCoBan(luongTheoCong);
        luong.setPhuCap(phuCap);
        luong.setThuong(thuong);
        luong.setKhauTruBaoHiem(baoHiem);
        luong.setKhauTruThue(thue);
        luong.setThucNhan(thucNhan);
        luong.setTrangThai(Luong.TrangThaiLuong.Draft);

        return luongRepo.save(luong);
    }

    @Transactional
    public Luong chotLuong(Integer luongId) {
        Luong l = luongRepo.findById(luongId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bang luong"));
        l.setTrangThai(Luong.TrangThaiLuong.DaDuyet);
        return luongRepo.save(l);
    }

    public List<Luong> bangLuongThang(int thang, int nam) {
        return luongRepo.findByThangAndNam(thang, nam);
    }

    public List<Luong> phieuLuongCuaToi(Integer nhanVienId) {
        return luongRepo.findByNhanVienId(nhanVienId);
    }
}
