package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.NhanVienRequest;
import com.hrm.entity.NhanVien;
import com.hrm.service.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService service;

    @GetMapping
    public ApiResponse<Page<NhanVien>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer phongBanId,
            @RequestParam(required = false) Integer chucVuId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(service.search(keyword, phongBanId, chucVuId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<NhanVien> get(@PathVariable Integer id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NhanVien> create(@Valid @RequestBody NhanVienRequest req) {
        return ApiResponse.ok("Them nhan vien thanh cong", service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NhanVien> update(@PathVariable Integer id, @Valid @RequestBody NhanVienRequest req) {
        return ApiResponse.ok("Cap nhat thanh cong", service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ApiResponse.ok("Da vo hieu hoa nhan vien", null);
    }
}
