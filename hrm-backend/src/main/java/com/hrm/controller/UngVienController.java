package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.UngVien;
import com.hrm.repository.UngVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recruitment/candidates")
@RequiredArgsConstructor
public class UngVienController {

    private final UngVienRepository repo;

    @GetMapping
    public ApiResponse<List<UngVien>> list() {
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<UngVien> create(@RequestBody UngVien uv) {
        uv.setId(null);
        if (uv.getTrangThai() == null) uv.setTrangThai("Moi");
        return ApiResponse.ok("Them ung vien thanh cong", repo.save(uv));
    }

    // Danh gia + doi trang thai ung vien
    @PutMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<UngVien> evaluate(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        UngVien uv = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khong tim thay ung vien"));
        if (body.get("diemDanhGia") != null) uv.setDiemDanhGia(Integer.parseInt(body.get("diemDanhGia").toString()));
        if (body.get("trangThai") != null) uv.setTrangThai((String) body.get("trangThai"));
        return ApiResponse.ok("Cap nhat danh gia thanh cong", repo.save(uv));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
