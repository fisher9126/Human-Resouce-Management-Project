package com.hrm.service;

import com.hrm.dto.DuyetDonRequest;
import com.hrm.dto.NghiPhepRequest;
import com.hrm.entity.NghiPhep;
import com.hrm.entity.NhanVien;
import com.hrm.repository.NghiPhepRepository;
import com.hrm.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NghiPhepService {

    private final NghiPhepRepository nghiPhepRepo;
    private final NhanVienRepository nhanVienRepo;

    @Transactional
    public NghiPhep guiDon(Integer nhanVienId, NghiPhepRequest req) {
        NhanVien nv = nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));

        if (req.getNgayKetThuc().isBefore(req.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngay ket thuc phai sau ngay bat dau");
        }

        NghiPhep.LoaiNghi loai = NghiPhep.LoaiNghi.valueOf(req.getLoaiNghi());

        // Kiem tra trung lich voi don da duyet
        List<NghiPhep> trung = nghiPhepRepo
                .findByNhanVienIdAndTrangThaiAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
                        nhanVienId, NghiPhep.TrangThaiDon.Duyet, req.getNgayKetThuc(), req.getNgayBatDau());
        if (!trung.isEmpty()) {
            throw new IllegalStateException("Khoang thoi gian nay trung voi mot don nghi da duoc duyet");
        }

        // Kiem tra quy phep nam
        if (loai == NghiPhep.LoaiNghi.PhepNam) {
            double conLai = nv.getSoNgayPhepConLai() != null ? nv.getSoNgayPhepConLai() : 0;
            if (req.getSoNgayNghi() > conLai) {
                throw new IllegalStateException(
                        "So ngay phep nam con lai khong du (Con " + conLai + " ngay). Hay chuyen sang Nghi khong luong!");
            }
        }

        NghiPhep don = NghiPhep.builder()
                .nhanVien(nv)
                .loaiNghi(loai)
                .ngayBatDau(req.getNgayBatDau())
                .ngayKetThuc(req.getNgayKetThuc())
                .soNgayNghi(req.getSoNgayNghi())
                .lyDo(req.getLyDo())
                .trangThai(NghiPhep.TrangThaiDon.ChoDuyet)
                .build();
        return nghiPhepRepo.save(don);
    }

    @Transactional
    public NghiPhep duyet(Integer donId, Integer nguoiDuyetId, DuyetDonRequest req) {
        NghiPhep don = nghiPhepRepo.findById(donId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don nghi"));

        if (don.getTrangThai() != NghiPhep.TrangThaiDon.ChoDuyet) {
            throw new IllegalStateException("Don nay da duoc xu ly boi nguoi khac!");
        }
        if (Boolean.FALSE.equals(req.getDuyet())
                && (req.getPhanHoi() == null || req.getPhanHoi().isBlank())) {
            throw new IllegalArgumentException("Phai nhap ly do khi tu choi don");
        }

        don.setNguoiDuyetId(nguoiDuyetId);
        don.setPhanHoiNguoiDuyet(req.getPhanHoi());

        if (Boolean.TRUE.equals(req.getDuyet())) {
            don.setTrangThai(NghiPhep.TrangThaiDon.Duyet);
            // Tru quy phep nam neu la nghi phep nam
            if (don.getLoaiNghi() == NghiPhep.LoaiNghi.PhepNam) {
                NhanVien nv = don.getNhanVien();
                double conLai = nv.getSoNgayPhepConLai() != null ? nv.getSoNgayPhepConLai() : 0;
                nv.setSoNgayPhepConLai(conLai - don.getSoNgayNghi());
                nhanVienRepo.save(nv);
            }
        } else {
            don.setTrangThai(NghiPhep.TrangThaiDon.TuChoi);
        }
        return nghiPhepRepo.save(don);
    }

    public List<NghiPhep> cuaToi(Integer nhanVienId) {
        return nghiPhepRepo.findByNhanVienId(nhanVienId);
    }

    public List<NghiPhep> choDuyet() {
        return nghiPhepRepo.findByTrangThai(NghiPhep.TrangThaiDon.ChoDuyet);
    }

    // Manager: chi don cho duyet cua phong minh
    public List<NghiPhep> choDuyetTheoPhongBan(Integer phongBanId) {
        return nghiPhepRepo.findByTrangThaiAndPhongBan(NghiPhep.TrangThaiDon.ChoDuyet, phongBanId);
    }

    // Kiem tra don co thuoc phong ban cua manager khong
    public void checkPhongBan(Integer donId, Integer phongBanIdManager) {
        if (phongBanIdManager == null) return;
        NghiPhep don = nghiPhepRepo.findById(donId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don"));
        Integer pb = don.getNhanVien().getPhongBan() != null ? don.getNhanVien().getPhongBan().getId() : null;
        if (!phongBanIdManager.equals(pb)) {
            throw new IllegalStateException("Ban chi duyet don cua nhan vien phong ban minh");
        }
    }
}
