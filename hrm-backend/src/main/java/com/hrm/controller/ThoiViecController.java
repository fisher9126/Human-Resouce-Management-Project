package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.NhanVien;
import com.hrm.entity.ThoiViec;
import com.hrm.repository.NhanVienRepository;
import com.hrm.repository.ThoiViecRepository;
import com.hrm.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resignations")
@RequiredArgsConstructor
public class ThoiViecController {

    private final ThoiViecRepository repo;
    private final NhanVienRepository nhanVienRepo;

    @GetMapping
    public ApiResponse<List<ThoiViec>> list(@AuthenticationPrincipal CustomUserDetails user) {
        if (user.isManager() && user.getPhongBanId() != null) {
            return ApiResponse.ok(repo.findByPhongBan(user.getPhongBanId()));
        }
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<ThoiViec> create(@RequestBody Map<String, Object> body) {
        NhanVien nv = nhanVienRepo.findById((Integer) body.get("nhanVienId"))
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        ThoiViec tv = ThoiViec.builder()
                .nhanVien(nv)
                .lyDo((String) body.get("lyDo"))
                .trangThai(body.get("trangThai") != null ? (String) body.get("trangThai") : "ChoDuyet")
                .ngayThoiViec(body.get("ngayThoiViec") != null ? LocalDate.parse(body.get("ngayThoiViec").toString()) : LocalDate.now())
                .build();
        return ApiResponse.ok("Them thoi viec thanh cong", repo.save(tv));
    }

    // Duyet thoi viec -> cap nhat trang thai NV thanh DaNghiViec
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ThoiViec> approve(@PathVariable Integer id) {
        ThoiViec tv = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don"));
        tv.setTrangThai("DaDuyet");
        NhanVien nv = tv.getNhanVien();
        nv.setTrangThai(NhanVien.TrangThaiNhanVien.DaNghiViec);
        nhanVienRepo.save(nv);
        return ApiResponse.ok("Da duyet thoi viec", repo.save(tv));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
