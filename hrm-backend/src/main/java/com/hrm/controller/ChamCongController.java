package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.ChamCong;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.ChamCongService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class ChamCongController {

    private final ChamCongService service;

    @PostMapping("/check-in")
    public ApiResponse<ChamCong> checkIn(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok("Ghi nhan Check-in thanh cong", service.checkIn(user.getNhanVienId()));
    }

    @PostMapping("/check-out")
    public ApiResponse<ChamCong> checkOut(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok("Ghi nhan Check-out thanh cong", service.checkOut(user.getNhanVienId()));
    }

    // Lich su cham cong theo thang cua chinh minh
    @GetMapping("/me")
    public ApiResponse<List<ChamCong>> myHistory(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam int thang, @RequestParam int nam) {
        return ApiResponse.ok(service.lichSuThang(user.getNhanVienId(), thang, nam));
    }

    // Admin/HR xem cham cong cua NV bat ky
    @GetMapping("/{nhanVienId}")
    public ApiResponse<Map<String, Object>> history(
            @PathVariable Integer nhanVienId,
            @RequestParam int thang, @RequestParam int nam) {
        List<ChamCong> ds = service.lichSuThang(nhanVienId, thang, nam);
        double tongCong = service.tongNgayCong(nhanVienId, thang, nam);
        return ApiResponse.ok(Map.of("lichSu", ds, "tongNgayCong", tongCong));
    }
}
