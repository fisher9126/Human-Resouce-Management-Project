package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.LichLamViec;
import com.hrm.entity.NhanVien;
import com.hrm.repository.LichLamViecRepository;
import com.hrm.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class LichLamViecController {

    private final LichLamViecRepository repo;
    private final NhanVienRepository nhanVienRepo;

    @GetMapping
    public ApiResponse<List<LichLamViec>> list() {
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<LichLamViec> create(@RequestBody Map<String, Object> body) {
        NhanVien nv = nhanVienRepo.findById((Integer) body.get("nhanVienId"))
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
        LichLamViec ll = LichLamViec.builder()
                .nhanVien(nv)
                .caLamViec((String) body.get("caLamViec"))
                .kieuNgay(body.get("kieuNgay") != null ? (String) body.get("kieuNgay") : "Ngay thuong")
                .ngayLamViec(body.get("ngayLamViec") != null ? LocalDate.parse(body.get("ngayLamViec").toString()) : LocalDate.now())
                .build();
        return ApiResponse.ok("Them lich lam viec thanh cong", repo.save(ll));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
