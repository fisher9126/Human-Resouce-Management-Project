package com.hrm.controller;

import com.hrm.dto.ApiResponse;
import com.hrm.dto.LoginRequest;
import com.hrm.dto.LoginResponse;
import com.hrm.security.CustomUserDetails;
import com.hrm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Dang nhap thanh cong", authService.login(request));
    }

    // Cap token moi khi user con hoat dong (idle timeout xu ly o frontend)
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok("Gia han phien thanh cong", authService.refresh(user.getUsername()));
    }
}
