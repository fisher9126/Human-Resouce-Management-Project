package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.KhenThuong;
import com.hrm.entity.NhanVien;
import com.hrm.repository.KhenThuongRepository;
import com.hrm.repository.NhanVienRepository;
import com.hrm.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class KhenThuongController {

    private final KhenThuongRepository repo;
    private final NhanVienRepository nhanVienRepo;

    @GetMapping
    public ApiResponse<List<KhenThuong>> list(@AuthenticationPrincipal CustomUserDetails user) {
        if (user.isManager() && user.getPhongBanId() != null) {
            return ApiResponse.ok(repo.findByPhongBan(user.getPhongBanId()));
        }
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<KhenThuong> create(@RequestBody Map<String, Object> body) {
        Integer nhanVienId = (Integer) body.get("nhanVienId");
        NhanVien nv = nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        KhenThuong kt = KhenThuong.builder()
                .nhanVien(nv)
                .lyDo((String) body.get("lyDo"))
                .hinhThuc((String) body.get("hinhThuc"))
                .soTien(body.get("soTien") != null ? new java.math.BigDecimal(body.get("soTien").toString()) : null)
                .ngayKhenThuong(body.get("ngayKhenThuong") != null ? java.time.LocalDate.parse(body.get("ngayKhenThuong").toString()) : java.time.LocalDate.now())
                .build();
        return ApiResponse.ok("Them khen thuong thanh cong", repo.save(kt));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
