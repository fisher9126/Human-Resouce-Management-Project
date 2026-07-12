package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.HopDong;
import com.hrm.service.HopDongService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class HopDongController {

    private final HopDongService service;

    @PostMapping("/employee/{nhanVienId}")
    public ApiResponse<HopDong> create(@PathVariable Integer nhanVienId, @RequestBody HopDong body) {
        return ApiResponse.ok("Them hop dong thanh cong", service.create(nhanVienId, body));
    }

    @GetMapping("/employee/{nhanVienId}")
    public ApiResponse<List<HopDong>> cuaNhanVien(@PathVariable Integer nhanVienId) {
        return ApiResponse.ok(service.cuaNhanVien(nhanVienId));
    }

    @GetMapping
    public ApiResponse<List<HopDong>> tatCa() {
        return ApiResponse.ok(service.tatCa());
    }

    @GetMapping("/expiring")
    public ApiResponse<List<HopDong>> sapHetHan() {
        return ApiResponse.ok("Danh sach hop dong sap het han trong 30 ngay", service.sapHetHan());
    }
}
