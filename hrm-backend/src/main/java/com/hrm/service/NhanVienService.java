package com.hrm.service;

import com.hrm.dto.NhanVienRequest;
import com.hrm.entity.ChucVu;
import com.hrm.entity.NhanVien;
import com.hrm.entity.PhongBan;
import com.hrm.entity.TaiKhoan;
import com.hrm.repository.ChucVuRepository;
import com.hrm.repository.NhanVienRepository;
import com.hrm.repository.PhongBanRepository;
import com.hrm.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NhanVienService {

    private final NhanVienRepository nhanVienRepo;
    private final PhongBanRepository phongBanRepo;
    private final ChucVuRepository chucVuRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final PasswordEncoder passwordEncoder;

    private static final String MAT_KHAU_MAC_DINH = "123456";

    public Page<NhanVien> search(String keyword, Integer phongBanId, Integer chucVuId,
                                 TaiKhoan.VaiTro vaiTro, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return nhanVienRepo.search(kw, phongBanId, chucVuId, vaiTro, pageable);
    }

    public NhanVien getById(Integer id) {
        return nhanVienRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien id=" + id));
    }

    // Kiem tra Manager chi duoc thao tac trong phong ban cua minh
    public void checkPhongBanQuyen(NhanVien nv, Integer phongBanIdCuaManager) {
        if (phongBanIdCuaManager == null) return; // Admin (khong gioi han)
        Integer pbNv = nv.getPhongBan() != null ? nv.getPhongBan().getId() : null;
        if (!phongBanIdCuaManager.equals(pbNv)) {
            throw new IllegalStateException("Ban chi duoc quan ly nhan vien trong phong ban cua minh");
        }
    }

    @Transactional
    public NhanVien create(NhanVienRequest req) {
        validateUnique(req, null);
        NhanVien nv = new NhanVien();
        apply(nv, req);
        nv = nhanVienRepo.save(nv);

        // Tu dong tao tai khoan: username = email, mat khau mac dinh 123456, vai tro nhan vien
        if (!taiKhoanRepo.existsByUsername(nv.getEmail())) {
            TaiKhoan tk = TaiKhoan.builder()
                    .username(nv.getEmail())
                    .passwordHash(passwordEncoder.encode(MAT_KHAU_MAC_DINH))
                    .vaiTro(TaiKhoan.VaiTro.ROLE_EMPLOYEE)
                    .active(true)
                    .nhanVien(nv)
                    .build();
            taiKhoanRepo.save(tk);
        }
        return nv;
    }

    @Transactional
    public NhanVien update(Integer id, NhanVienRequest req) {
        NhanVien nv = getById(id);
        validateUnique(req, id);
        apply(nv, req);
        return nhanVienRepo.save(nv);
    }

    @Transactional
    public void delete(Integer id) {
        NhanVien nv = getById(id);
        // Vo hieu hoa thay vi xoa cung de giu lich su
        nv.setTrangThai(NhanVien.TrangThaiNhanVien.DaNghiViec);
        nhanVienRepo.save(nv);
    }

    private void validateUnique(NhanVienRequest req, Integer excludeId) {
        nhanVienRepo.findByCccd(req.getCccd()).ifPresent(x -> {
            if (!x.getId().equals(excludeId)) throw new IllegalStateException("CCCD da ton tai");
        });
        nhanVienRepo.findByEmail(req.getEmail()).ifPresent(x -> {
            if (!x.getId().equals(excludeId)) throw new IllegalStateException("Email da ton tai");
        });
        nhanVienRepo.findBySdt(req.getSdt()).ifPresent(x -> {
            if (!x.getId().equals(excludeId)) throw new IllegalStateException("SDT da ton tai");
        });
    }

    private void apply(NhanVien nv, NhanVienRequest req) {
        nv.setHoTen(req.getHoTen());
        nv.setNgaySinh(req.getNgaySinh());
        nv.setGioiTinh(req.getGioiTinh());
        nv.setCccd(req.getCccd());
        nv.setSdt(req.getSdt());
        nv.setEmail(req.getEmail());
        nv.setDiaChi(req.getDiaChi());
        nv.setAnh(req.getAnh());
        nv.setNgayVaoLam(req.getNgayVaoLam());
        if (req.getTrangThai() != null && !req.getTrangThai().isBlank()) {
            nv.setTrangThai(NhanVien.TrangThaiNhanVien.valueOf(req.getTrangThai()));
        }
        if (req.getPhongBanId() != null) {
            PhongBan pb = phongBanRepo.findById(req.getPhongBanId())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phong ban"));
            nv.setPhongBan(pb);
        }
        if (req.getChucVuId() != null) {
            ChucVu cv = chucVuRepo.findById(req.getChucVuId())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chuc vu"));
            nv.setChucVu(cv);
        }
    }
}
