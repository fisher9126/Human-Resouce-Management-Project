package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.BoNhiem;
import com.hrm.entity.NhanVien;
import com.hrm.repository.BoNhiemRepository;
import com.hrm.repository.NhanVienRepository;
import com.hrm.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class BoNhiemController {

    private final BoNhiemRepository repo;
    private final NhanVienRepository nhanVienRepo;

    @GetMapping
    public ApiResponse<List<BoNhiem>> list(@AuthenticationPrincipal CustomUserDetails user) {
        if (user.isManager() && user.getPhongBanId() != null) {
            return ApiResponse.ok(repo.findByPhongBan(user.getPhongBanId()));
        }
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<BoNhiem> create(@RequestBody Map<String, Object> body) {
        NhanVien nv = nhanVienRepo.findById((Integer) body.get("nhanVienId"))
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        BoNhiem bn = BoNhiem.builder()
                .nhanVien(nv)
                .tuChucVu((String) body.get("tuChucVu"))
                .toiChucVu((String) body.get("toiChucVu"))
                .tuPhongBan((String) body.get("tuPhongBan"))
                .toiPhongBan((String) body.get("toiPhongBan"))
                .lyDo((String) body.get("lyDo"))
                .ngayBoNhiem(body.get("ngayBoNhiem") != null ? LocalDate.parse(body.get("ngayBoNhiem").toString()) : LocalDate.now())
                .build();
        return ApiResponse.ok("Them bo nhiem thanh cong", repo.save(bn));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
