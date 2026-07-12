package com.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo userInfo;

    @Getter @Setter @Builder @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String username;
        private String hoTen;
        private String email;
        private String vaiTro;
        private Integer nhanVienId;
        private String imageUrl;
    }
}
