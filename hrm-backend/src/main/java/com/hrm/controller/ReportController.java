package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.entity.NghiPhep;
import com.hrm.entity.PhongBan;
import com.hrm.repository.*;
import com.hrm.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // Dashboard tong quan (Manager chi thay phong minh)
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Map<String, Object>> dashboard(@AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> data = new LinkedHashMap<>();
        if (user.isManager()) {
            Integer pbId = user.getPhongBanId();
            long soNV = pbId != null ? nhanVienRepo.countByPhongBanId(pbId) : 0;
            long donCho = pbId != null ? nghiPhepRepo.countByTrangThaiAndPhongBan(NghiPhep.TrangThaiDon.ChoDuyet, pbId) : 0;
            data.put("tongNhanVien", soNV);
            data.put("tongPhongBan", 1);
            data.put("donNghiChoDuyet", donCho);
            String tenPb = pbId != null ? phongBanRepo.findById(pbId).map(PhongBan::getTenPhongBan).orElse("") : "";
            data.put("tenPhongBan", tenPb);
        } else {
            data.put("tongNhanVien", nhanVienRepo.count());
            data.put("tongPhongBan", phongBanRepo.count());
            data.put("donNghiChoDuyet", nghiPhepRepo.countByTrangThai(NghiPhep.TrangThaiDon.ChoDuyet));
        }
        return ApiResponse.ok(data);
    }

    // Co cau nhan vien theo phong ban (Manager chi phong minh)
    @GetMapping("/employees-by-department")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<List<Map<String, Object>>> nhanVienTheoPhongBan(@AuthenticationPrincipal CustomUserDetails user) {
        List<PhongBan> dsPb;
        if (user.isManager() && user.getPhongBanId() != null) {
            dsPb = phongBanRepo.findById(user.getPhongBanId()).map(List::of).orElse(List.of());
        } else {
            dsPb = phongBanRepo.findAll();
        }
        return ApiResponse.ok(dsPb.stream().map(this::thongKePhongBan).toList());
    }

    private Map<String, Object> thongKePhongBan(PhongBan pb) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("phongBan", pb.getTenPhongBan());
        m.put("soNhanVien", nhanVienRepo.countByPhongBanId(pb.getId()));
        return m;
    }

    // Tong quy luong theo thang (Manager chi phong minh)
    @GetMapping("/payroll-summary")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<Map<String, Object>> tongQuyLuong(@AuthenticationPrincipal CustomUserDetails user,
                                                         @RequestParam int thang, @RequestParam int nam) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("thang", thang);
        m.put("nam", nam);
        if (user.isManager() && user.getPhongBanId() != null) {
            BigDecimal tong = luongRepo.tongQuyLuongTheoPhongBan(thang, nam, user.getPhongBanId());
            m.put("tongQuyLuong", tong != null ? tong : BigDecimal.ZERO);
            m.put("soBangLuong", luongRepo.demBangLuongTheoPhongBan(thang, nam, user.getPhongBanId()));
        } else {
            BigDecimal tong = luongRepo.tongQuyLuong(thang, nam);
            m.put("tongQuyLuong", tong != null ? tong : BigDecimal.ZERO);
            m.put("soBangLuong", luongRepo.findByThangAndNam(thang, nam).size());
        }
        return ApiResponse.ok(m);
    }
}
