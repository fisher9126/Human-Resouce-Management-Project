package com.hrm.security;

import com.hrm.entity.TaiKhoan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final TaiKhoan taiKhoan;

    public CustomUserDetails(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public Integer getNhanVienId() {
        return taiKhoan.getNhanVien() != null ? taiKhoan.getNhanVien().getId() : null;
    }

    // Phong ban cua nguoi dang dang nhap (dung de gioi han pham vi cua Manager)
    public Integer getPhongBanId() {
        if (taiKhoan.getNhanVien() == null || taiKhoan.getNhanVien().getPhongBan() == null) return null;
        return taiKhoan.getNhanVien().getPhongBan().getId();
    }

    public boolean isAdmin() {
        return taiKhoan.getVaiTro() == TaiKhoan.VaiTro.ROLE_ADMIN;
    }

    public boolean isManager() {
        return taiKhoan.getVaiTro() == TaiKhoan.VaiTro.ROLE_MANAGER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(taiKhoan.getVaiTro().name()));
    }

    @Override
    public String getPassword() {
        return taiKhoan.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return taiKhoan.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(taiKhoan.getActive());
    }
}
