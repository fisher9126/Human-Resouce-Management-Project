package com.hrm.service;

import com.hrm.entity.HopDong;
import com.hrm.entity.NhanVien;
import com.hrm.repository.HopDongRepository;
import com.hrm.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HopDongService {

    private final HopDongRepository hopDongRepo;
    private final NhanVienRepository nhanVienRepo;

    @Transactional
    public HopDong create(Integer nhanVienId, HopDong body) {
        NhanVien nv = nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        if (body.getSoHopDong() != null && hopDongRepo.existsBySoHopDong(body.getSoHopDong())) {
            throw new IllegalStateException("So hop dong da ton tai");
        }
        body.setId(null);
        body.setNhanVien(nv);
        if (body.getTrangThai() == null) body.setTrangThai(HopDong.TrangThaiHopDong.ConHieuLuc);
        return hopDongRepo.save(body);
    }

    public List<HopDong> cuaNhanVien(Integer nhanVienId) {
        return hopDongRepo.findByNhanVienId(nhanVienId);
    }

    public List<HopDong> tatCa() {
        return hopDongRepo.findAll();
    }

    // Canh bao: hop dong con hieu luc va het han trong vong 30 ngay toi
    public List<HopDong> sapHetHan() {
        LocalDate moc = LocalDate.now().plusDays(30);
        return hopDongRepo.findByTrangThaiAndNgayHetHanLessThanEqual(
                HopDong.TrangThaiHopDong.ConHieuLuc, moc);
    }
}
