package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.TinhLuongRequest;
import com.hrm.entity.Luong;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.LuongService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class LuongController {

    private final LuongService service;

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<List<Luong>> tinhLuong(@AuthenticationPrincipal CustomUserDetails user,
                                              @RequestBody TinhLuongRequest req) {
        Integer pbId = user.isManager() ? user.getPhongBanId() : null;
        return ApiResponse.ok("Tinh luong thanh cong (trang thai Draft)",
                service.tinhLuongToanCongTy(req, pbId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Luong> chot(@PathVariable Integer id) {
        return ApiResponse.ok("Da chot bang luong", service.chotLuong(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<List<Luong>> bangLuong(@AuthenticationPrincipal CustomUserDetails user,
                                              @RequestParam int thang, @RequestParam int nam) {
        if (user.isManager() && user.getPhongBanId() != null) {
            return ApiResponse.ok(service.bangLuongThangTheoPhongBan(thang, nam, user.getPhongBanId()));
        }
        return ApiResponse.ok(service.bangLuongThang(thang, nam));
    }

    // Nhan vien xem phieu luong cua chinh minh
    @GetMapping("/me")
    public ApiResponse<List<Luong>> cuaToi(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(service.phieuLuongCuaToi(user.getNhanVienId()));
    }
}
