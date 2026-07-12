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
    public ApiResponse<List<Luong>> tinhLuong(@RequestBody TinhLuongRequest req) {
        return ApiResponse.ok("Tinh luong thanh cong (trang thai Draft)",
                service.tinhLuongToanCongTy(req));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Luong> chot(@PathVariable Integer id) {
        return ApiResponse.ok("Da chot bang luong", service.chotLuong(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<List<Luong>> bangLuong(@RequestParam int thang, @RequestParam int nam) {
        return ApiResponse.ok(service.bangLuongThang(thang, nam));
    }

    // Nhan vien xem phieu luong cua chinh minh
    @GetMapping("/me")
    public ApiResponse<List<Luong>> cuaToi(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(service.phieuLuongCuaToi(user.getNhanVienId()));
    }
}
