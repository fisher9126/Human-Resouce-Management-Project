package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.NghiPhep;
import com.hrm.entity.PhongBan;
import com.hrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final NhanVienRepository nhanVienRepo;
    private final PhongBanRepository phongBanRepo;
    private final NghiPhepRepository nghiPhepRepo;
    private final LuongRepository luongRepo;

    // Dashboard tong quan
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tongNhanVien", nhanVienRepo.count());
        data.put("tongPhongBan", phongBanRepo.count());
        data.put("donNghiChoDuyet", nghiPhepRepo.countByTrangThai(NghiPhep.TrangThaiDon.ChoDuyet));
        return ApiResponse.ok(data);
    }

    // Co cau nhan vien theo phong ban
    @GetMapping("/employees-by-department")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<List<Map<String, Object>>> nhanVienTheoPhongBan() {
        List<Map<String, Object>> ketQua = phongBanRepo.findAll().stream()
                .map(this::thongKePhongBan)
                .toList();
        return ApiResponse.ok(ketQua);
    }

    private Map<String, Object> thongKePhongBan(PhongBan pb) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("phongBan", pb.getTenPhongBan());
        m.put("soNhanVien", nhanVienRepo.countByPhongBanId(pb.getId()));
        return m;
    }

    // Tong quy luong theo thang
    @GetMapping("/payroll-summary")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Map<String, Object>> tongQuyLuong(@RequestParam int thang, @RequestParam int nam) {
        BigDecimal tong = luongRepo.tongQuyLuong(thang, nam);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("thang", thang);
        m.put("nam", nam);
        m.put("tongQuyLuong", tong);
        m.put("soBangLuong", luongRepo.findByThangAndNam(thang, nam).size());
        return ApiResponse.ok(m);
    }
}
