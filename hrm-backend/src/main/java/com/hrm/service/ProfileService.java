package com.hrm.service;

import com.hrm.dto.DoiMatKhauRequest;
import com.hrm.dto.UpdateProfileRequest;
import com.hrm.entity.NhanVien;
import com.hrm.entity.TaiKhoan;
import com.hrm.repository.NhanVienRepository;
import com.hrm.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final TaiKhoanRepository taiKhoanRepo;
    private final NhanVienRepository nhanVienRepo;
    private final PasswordEncoder passwordEncoder;

    public NhanVien getProfile(Integer nhanVienId) {
        if (nhanVienId == null) throw new IllegalStateException("Tai khoan chua gan voi nhan vien");
        return nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay ho so"));
    }

    @Transactional
    public NhanVien updateProfile(Integer nhanVienId, UpdateProfileRequest req) {
        NhanVien nv = getProfile(nhanVienId);
        if (req.getSdt() != null) nv.setSdt(req.getSdt());
        if (req.getDiaChi() != null) nv.setDiaChi(req.getDiaChi());
        if (req.getAnh() != null) nv.setAnh(req.getAnh());
        return nhanVienRepo.save(nv);
    }

    @Transactional
    public void doiMatKhau(String username, DoiMatKhauRequest req) {
        if (!req.getMatKhauMoi().equals(req.getXacNhanMatKhau())) {
            throw new IllegalArgumentException("Xac nhan mat khau khong khop");
        }
        if (req.getMatKhauMoi().length() < 6) {
            throw new IllegalArgumentException("Mat khau moi phai co it nhat 6 ky tu");
        }
        TaiKhoan tk = taiKhoanRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));
        if (!passwordEncoder.matches(req.getMatKhauCu(), tk.getPasswordHash())) {
            throw new IllegalArgumentException("Mat khau cu khong dung");
        }
        tk.setPasswordHash(passwordEncoder.encode(req.getMatKhauMoi()));
        taiKhoanRepo.save(tk);
    }
}
