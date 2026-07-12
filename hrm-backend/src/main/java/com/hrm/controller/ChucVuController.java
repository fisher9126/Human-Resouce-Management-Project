package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.ChucVu;
import com.hrm.repository.ChucVuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class ChucVuController {

    private final ChucVuRepository repo;

    @GetMapping
    public ApiResponse<List<ChucVu>> list() {
        return ApiResponse.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ChucVu> get(@PathVariable Integer id) {
        return ApiResponse.ok(repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chuc vu")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ChucVu> create(@RequestBody ChucVu cv) {
        cv.setId(null);
        return ApiResponse.ok("Them chuc vu thanh cong", repo.save(cv));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ChucVu> update(@PathVariable Integer id, @RequestBody ChucVu body) {
        ChucVu cv = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chuc vu"));
        cv.setTenChucVu(body.getTenChucVu());
        cv.setLuongCoBan(body.getLuongCoBan());
        cv.setPhuCapChucVu(body.getPhuCapChucVu());
        return ApiResponse.ok("Cap nhat thanh cong", repo.save(cv));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa chuc vu", null);
    }
}
