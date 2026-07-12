package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.PhongBan;
import com.hrm.repository.PhongBanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class PhongBanController {

    private final PhongBanRepository repo;

    @GetMapping
    public ApiResponse<List<PhongBan>> list() {
        return ApiResponse.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<PhongBan> get(@PathVariable Integer id) {
        return ApiResponse.ok(repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phong ban")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PhongBan> create(@RequestBody PhongBan pb) {
        if (pb.getMaPhongBan() != null && repo.existsByMaPhongBan(pb.getMaPhongBan()))
            throw new IllegalStateException("Ma phong ban da ton tai");
        pb.setId(null);
        return ApiResponse.ok("Them phong ban thanh cong", repo.save(pb));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PhongBan> update(@PathVariable Integer id, @RequestBody PhongBan body) {
        PhongBan pb = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phong ban"));
        pb.setMaPhongBan(body.getMaPhongBan());
        pb.setTenPhongBan(body.getTenPhongBan());
        pb.setMoTa(body.getMoTa());
        pb.setTruongPhongId(body.getTruongPhongId());
        return ApiResponse.ok("Cap nhat thanh cong", repo.save(pb));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa phong ban", null);
    }
}
