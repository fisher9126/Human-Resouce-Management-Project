package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.ThongBao;
import com.hrm.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoRepository repo;

    // Danh sach thong bao gan nhat + so chua doc
    @GetMapping
    public ApiResponse<Map<String, Object>> list() {
        List<ThongBao> ds = repo.findTop20ByOrderByThoiGianDesc();
        long chuaDoc = repo.countByDaDocFalse();
        return ApiResponse.ok(Map.of("danhSach", ds, "chuaDoc", chuaDoc));
    }

    // Danh dau da doc 1 thong bao
    @PutMapping("/{id}/read")
    public ApiResponse<Void> read(@PathVariable Integer id) {
        repo.findById(id).ifPresent(tb -> { tb.setDaDoc(true); repo.save(tb); });
        return ApiResponse.ok("Da danh dau da doc", null);
    }

    // Danh dau tat ca da doc
    @PutMapping("/read-all")
    public ApiResponse<Void> readAll() {
        List<ThongBao> ds = repo.findByDaDocFalse();
        ds.forEach(tb -> tb.setDaDoc(true));
        repo.saveAll(ds);
        return ApiResponse.ok("Da danh dau tat ca da doc", null);
    }
}
