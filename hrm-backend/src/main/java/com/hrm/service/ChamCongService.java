package com.hrm.service;

import com.hrm.entity.ChamCong;
import com.hrm.entity.NhanVien;
import com.hrm.repository.ChamCongRepository;
import com.hrm.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChamCongService {

    private final ChamCongRepository chamCongRepo;
    private final NhanVienRepository nhanVienRepo;

    // Quy tac gio lam theo SRS
    private static final LocalTime GIO_VAO_CHUAN = LocalTime.of(8, 0);
    private static final LocalTime GIO_VAO_AN_HAN = LocalTime.of(8, 15); // grace 15 phut
    private static final LocalTime GIO_RA_CHUAN = LocalTime.of(17, 30);

    private NhanVien nv(Integer nhanVienId) {
        return nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
    }

    @Transactional
    public ChamCong checkIn(Integer nhanVienId) {
        LocalDate today = LocalDate.now();
        ChamCong cc = chamCongRepo.findByNhanVienIdAndNgay(nhanVienId, today).orElse(null);

        if (cc != null && cc.getGioVao() != null) {
            throw new IllegalStateException("Hom nay ban da check-in roi");
        }
        LocalTime now = LocalTime.now();
        if (cc == null) {
            cc = ChamCong.builder().nhanVien(nv(nhanVienId)).ngay(today).build();
        }
        cc.setGioVao(now);
        cc.setDiTre(now.isAfter(GIO_VAO_AN_HAN));
        cc.setTrangThai("HopLe");
        return chamCongRepo.save(cc);
    }

    @Transactional
    public ChamCong checkOut(Integer nhanVienId) {
        LocalDate today = LocalDate.now();
        ChamCong cc = chamCongRepo.findByNhanVienIdAndNgay(nhanVienId, today)
                .orElseThrow(() -> new IllegalStateException("Ban chua check-in hom nay"));
        if (cc.getGioRa() != null) {
            throw new IllegalStateException("Hom nay ban da check-out roi");
        }
        LocalTime now = LocalTime.now();
        cc.setGioRa(now);
        cc.setVeSom(now.isBefore(GIO_RA_CHUAN));
        // Tinh so gio lam thuc te
        if (cc.getGioVao() != null) {
            long phut = Duration.between(cc.getGioVao(), now).toMinutes();
            // tru 90 phut nghi trua neu lam qua buoi trua
            if (cc.getGioVao().isBefore(LocalTime.of(12, 0)) && now.isAfter(LocalTime.of(13, 30))) {
                phut -= 90;
            }
            cc.setSoGioLam(Math.max(0, phut) / 60.0f);
        }
        return chamCongRepo.save(cc);
    }

    public List<ChamCong> lichSuThang(Integer nhanVienId, int thang, int nam) {
        LocalDate tu = LocalDate.of(nam, thang, 1);
        LocalDate den = tu.withDayOfMonth(tu.lengthOfMonth());
        return chamCongRepo.findByNhanVienIdAndNgayBetween(nhanVienId, tu, den);
    }

    // Tong so ngay cong thuc te trong thang (moi ban ghi co gio ra = 1 cong, phat di tre/ve som tru 0.5)
    public double tongNgayCong(Integer nhanVienId, int thang, int nam) {
        List<ChamCong> ds = lichSuThang(nhanVienId, thang, nam);
        double tong = 0;
        for (ChamCong cc : ds) {
            if (cc.getGioRa() == null) continue;
            double cong = 1.0;
            if (Boolean.TRUE.equals(cc.getDiTre()) || Boolean.TRUE.equals(cc.getVeSom())) {
                cong -= 0.5; // phat don gian theo SRS (co the tinh chi tiet theo phut)
            }
            tong += Math.max(0, cong);
        }
        return tong;
    }
}
