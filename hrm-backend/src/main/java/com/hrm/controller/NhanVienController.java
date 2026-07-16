package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.NhanVienRequest;
import com.hrm.entity.NhanVien;
import com.hrm.entity.TaiKhoan;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService service;

    @GetMapping
    public ApiResponse<Page<NhanVien>> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer phongBanId,
            @RequestParam(required = false) Integer chucVuId,
            @RequestParam(required = false) String vaiTro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Manager: ep loc theo phong ban cua minh (bo qua phongBanId client gui)
        Integer pbFilter = phongBanId;
        if (user.isManager()) {
            pbFilter = user.getPhongBanId();
        }
        // Parse vai tro filter (chi Admin moi loc theo vai tro)
        TaiKhoan.VaiTro vt = null;
        if (user.isAdmin() && vaiTro != null && !vaiTro.isBlank()) {
            vt = TaiKhoan.VaiTro.valueOf(vaiTro);
        }
        return ApiResponse.ok(service.search(keyword, pbFilter, chucVuId, vt, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<NhanVien> get(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Integer id) {
        NhanVien nv = service.getById(id);
        if (user.isManager()) service.checkPhongBanQuyen(nv, user.getPhongBanId());
        return ApiResponse.ok(nv);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<NhanVien> create(@AuthenticationPrincipal CustomUserDetails user,
                                        @Valid @RequestBody NhanVienRequest req) {
        // Manager chi tao nhan vien trong phong ban cua minh
        if (user.isManager()) {
            req.setPhongBanId(user.getPhongBanId());
        }
        return ApiResponse.ok("Them nhan vien thanh cong", service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<NhanVien> update(@AuthenticationPrincipal CustomUserDetails user,
                                        @PathVariable Integer id, @Valid @RequestBody NhanVienRequest req) {
        if (user.isManager()) {
            service.checkPhongBanQuyen(service.getById(id), user.getPhongBanId());
            req.setPhongBanId(user.getPhongBanId()); // khong cho chuyen sang phong khac
        }
        return ApiResponse.ok("Cap nhat thanh cong", service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Integer id) {
        if (user.isManager()) service.checkPhongBanQuyen(service.getById(id), user.getPhongBanId());
        service.delete(id);
        return ApiResponse.ok("Da vo hieu hoa nhan vien", null);
    }
}
