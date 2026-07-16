package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.CaLamViec;
import com.hrm.repository.CaLamViecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class CaLamViecController {

    private final CaLamViecRepository repo;

    @GetMapping
    public ApiResponse<List<CaLamViec>> list() {
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<CaLamViec> create(@RequestBody CaLamViec ca) {
        ca.setId(null);
        return ApiResponse.ok("Them ca lam viec thanh cong", repo.save(ca));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<CaLamViec> update(@PathVariable Integer id, @RequestBody CaLamViec body) {
        CaLamViec ca = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khong tim thay ca"));
        ca.setTenCa(body.getTenCa());
        ca.setGioBatDau(body.getGioBatDau());
        ca.setGioKetThuc(body.getGioKetThuc());
        ca.setMoTa(body.getMoTa());
        return ApiResponse.ok("Cap nhat thanh cong", repo.save(ca));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
