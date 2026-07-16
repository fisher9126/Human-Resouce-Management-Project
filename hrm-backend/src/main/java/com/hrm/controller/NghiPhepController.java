package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.DuyetDonRequest;
import com.hrm.dto.NghiPhepRequest;
import com.hrm.entity.NghiPhep;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.NghiPhepService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class NghiPhepController {

    private final NghiPhepService service;

    @PostMapping("/request")
    public ApiResponse<NghiPhep> guiDon(@AuthenticationPrincipal CustomUserDetails user,
                                        @Valid @RequestBody NghiPhepRequest req) {
        return ApiResponse.ok("Gui don xin nghi phep thanh cong, dang cho phe duyet",
                service.guiDon(user.getNhanVienId(), req));
    }

    @GetMapping("/me")
    public ApiResponse<List<NghiPhep>> cuaToi(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(service.cuaToi(user.getNhanVienId()));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<List<NghiPhep>> choDuyet(@AuthenticationPrincipal CustomUserDetails user) {
        if (user.isManager() && user.getPhongBanId() != null) {
            return ApiResponse.ok(service.choDuyetTheoPhongBan(user.getPhongBanId()));
        }
        return ApiResponse.ok(service.choDuyet());
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<NghiPhep> duyet(@AuthenticationPrincipal CustomUserDetails user,
                                       @PathVariable Integer id,
                                       @Valid @RequestBody DuyetDonRequest req) {
        if (user.isManager()) service.checkPhongBan(id, user.getPhongBanId());
        NghiPhep don = service.duyet(id, user.getNhanVienId(), req);
        String msg = Boolean.TRUE.equals(req.getDuyet())
                ? "Da phe duyet don nghi phep thanh cong" : "Da tu choi don nghi phep";
        return ApiResponse.ok(msg, don);
    }
}
