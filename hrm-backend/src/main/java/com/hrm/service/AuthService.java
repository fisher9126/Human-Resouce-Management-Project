package com.hrm.service;

import com.hrm.dto.LoginRequest;
import com.hrm.dto.LoginResponse;
import com.hrm.entity.TaiKhoan;
import com.hrm.repository.TaiKhoanRepository;
import com.hrm.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TaiKhoanRepository taiKhoanRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {
        // Xac thuc username/password (nem BadCredentialsException neu sai)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        TaiKhoan tk = taiKhoanRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));

        return buildResponse(tk);
    }

    /** Cap token moi cho user dang dang nhap (frontend goi khi con hoat dong). */
    public LoginResponse refresh(String username) {
        TaiKhoan tk = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));
        return buildResponse(tk);
    }

    private LoginResponse buildResponse(TaiKhoan tk) {
        Integer nhanVienId = tk.getNhanVien() != null ? tk.getNhanVien().getId() : null;
        String hoTen = tk.getNhanVien() != null ? tk.getNhanVien().getHoTen() : null;
        String email = tk.getNhanVien() != null ? tk.getNhanVien().getEmail() : null;
        String imageUrl = tk.getNhanVien() != null ? tk.getNhanVien().getAnh() : null;

        String token = jwtService.generateToken(tk.getUsername(), tk.getVaiTro().name(), nhanVienId);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(tk.getId())
                        .username(tk.getUsername())
                        .hoTen(hoTen)
                        .email(email)
                        .vaiTro(tk.getVaiTro().name())
                        .nhanVienId(nhanVienId)
                        .imageUrl(imageUrl)
                        .build())
                .build();
    }
}
