package com.hrm.security;

import com.hrm.entity.TaiKhoan;
import com.hrm.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan tk = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay tai khoan: " + username));
        return new CustomUserDetails(tk);
    }
}
