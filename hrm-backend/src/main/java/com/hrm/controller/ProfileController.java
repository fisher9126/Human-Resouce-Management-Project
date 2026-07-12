package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.DoiMatKhauRequest;
import com.hrm.dto.UpdateProfileRequest;
import com.hrm.entity.NhanVien;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.CloudinaryService;
import com.hrm.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ApiResponse<NhanVien> getProfile(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(service.getProfile(user.getNhanVienId()));
    }

    @PutMapping
    public ApiResponse<NhanVien> updateProfile(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody UpdateProfileRequest req) {
        return ApiResponse.ok("Cap nhat thong tin thanh cong", service.updateProfile(user.getNhanVienId(), req));
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> doiMatKhau(@AuthenticationPrincipal CustomUserDetails user,
                                        @Valid @RequestBody DoiMatKhauRequest req) {
        service.doiMatKhau(user.getUsername(), req);
        return ApiResponse.ok("Doi mat khau thanh cong", null);
    }

    // Upload avatar qua Cloudinary, luu URL vao ho so
    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(@AuthenticationPrincipal CustomUserDetails user,
                                                         @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadAnh(file);
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setAnh(url);
        service.updateProfile(user.getNhanVienId(), req);
        return ApiResponse.ok("Cap nhat anh dai dien thanh cong", Map.of("imageUrl", url));
    }
}
