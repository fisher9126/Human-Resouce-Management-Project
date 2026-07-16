package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.TinTuyenDung;
import com.hrm.repository.TinTuyenDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recruitment/posts")
@RequiredArgsConstructor
public class TinTuyenDungController {

    private final TinTuyenDungRepository repo;

    @GetMapping
    public ApiResponse<List<TinTuyenDung>> list() {
        return ApiResponse.ok(repo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<TinTuyenDung> create(@RequestBody TinTuyenDung tin) {
        tin.setId(null);
        if (tin.getHieuLuc() == null) tin.setHieuLuc(true);
        return ApiResponse.ok("Dang tin tuyen dung thanh cong", repo.save(tin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<TinTuyenDung> update(@PathVariable Integer id, @RequestBody TinTuyenDung body) {
        TinTuyenDung tin = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Khong tim thay tin"));
        tin.setTieuDe(body.getTieuDe());
        tin.setDiaDiem(body.getDiaDiem());
        tin.setNgayDang(body.getNgayDang());
        tin.setHanNop(body.getHanNop());
        tin.setHieuLuc(body.getHieuLuc());
        tin.setMoTa(body.getMoTa());
        return ApiResponse.ok("Cap nhat thanh cong", repo.save(tin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        repo.deleteById(id);
        return ApiResponse.ok("Da xoa", null);
    }
}
